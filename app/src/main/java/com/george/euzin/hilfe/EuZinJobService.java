package com.george.euzin.hilfe;


import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by farmaker1 on 20/12/2017.
 */

public class EuZinJobService extends JobService {

    private EuZinDownloadFunction mEuZinDownload=new EuZinDownloadFunction();
    private AsyncTask<Void,Void,Void> mDownloadFromInternet;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mDownloadFromInternet = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();

                //Execute this method to download picture
                mEuZinDownload.sendIntentToDownloadPicture(context);

                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mDownloadFromInternet.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mDownloadFromInternet != null) {
            mDownloadFromInternet.cancel(true);
        }
        return true;
    }
}
