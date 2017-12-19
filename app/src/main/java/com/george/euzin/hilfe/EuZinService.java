package com.george.euzin.hilfe;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.george.euzin.MainActivity;
import com.george.euzin.data.EuZinContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class EuZinService extends IntentService {

    private EuZinDownloadFunction mEuZinDownload=new EuZinDownloadFunction();


    public EuZinService() {
        super("EuZinService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mEuZinDownload.downloadFromInternet();
    }
}
