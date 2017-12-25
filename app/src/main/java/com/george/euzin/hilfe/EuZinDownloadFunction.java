package com.george.euzin.hilfe;

import android.content.Context;
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

/**
 * Created by farmaker1 on 19/12/2017.
 */

public class EuZinDownloadFunction {

    private static final String NUMBER_OF_RECEIVER = "updating";
    private static final String DOWNLOAD_OF_RECEIVER = "downloading";

    public void downloadFromInternet(Context context){
        String urlToUse = "https://firebasestorage.googleapis.com/v0/b/snow-1557b.appspot.com/o/mainGrid.db?alt=media&token=cd00fc04-fa62-420b-aee8-9bb05d163d3d";

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        /*String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Recipe-DB";*/
        String path = EuZinContract.MainGrid.DB_PATH;

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        try {
            URL url = new URL(urlToUse);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage(), "server");
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            File fToPut = new File(dir, "mainGrid.db");
            /*File fToPut = new File(dir, "3.jpeg");*/

            /// set Append to false if you want to overwrite
            output = new FileOutputStream(fToPut, false);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                        /*if (isCancelled()) {
                            input.close();
                            return null;
                        }*/
                total += count;
                // publishing the progress....
                        /*if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));*/
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        Log.e("DEMO","yew");

        Intent intent = new Intent();
        intent.setAction(NUMBER_OF_RECEIVER);
        context.sendBroadcast(intent);

    }

    public void sendIntentToDownloadPicture(Context context){
        Intent intent = new Intent();
        intent.setAction(DOWNLOAD_OF_RECEIVER);
        context.sendBroadcast(intent);
    }
}
