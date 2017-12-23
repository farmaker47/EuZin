package com.george.euzin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.george.euzin.data.EuZinContract;
import com.george.euzin.data.EuZinMainGridDbHelper;
import com.george.euzin.hilfe.EuZinJobDispatcher;
import com.george.euzin.hilfe.EuZinService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>, EuZinMainAdapter.euZinClickItemListener {

    private RecyclerView mRecyclerView;
    private EuZinMainAdapter mEuZinAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SQLiteDatabase mDb;
    private EuZinMainGridDbHelper dbHelper;
    private static final int MAIN_LOADER = 23;
    public static final String NUMBER_OF_GRID = "number";
    private static final String NUMBER_OF_RECEIVER = "updating";
    private static final String DOWNLOAD_OF_RECEIVER = "downloading";
    private static final int NOTIFICATION_ID = 4000;

    private static final int DATABASE_LOADER = 42;
    private static final String URL_TO_DOWNLOAD = "https://firebasestorage.googleapis.com/v0/b/snow-1557b.appspot.com/o/rrecip.jpg?alt=media&token=e093fbe1-a4c9-4f7b-b6fa-eb262221607d";
    public static final String URL_KEY = "urlKey";

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mFilter;

    private android.support.v4.app.LoaderManager.LoaderCallbacks mLoaderCallBackString = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(MainActivity.this) {

                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }

                    forceLoad();
                }

                @Override
                public String loadInBackground() {

                    String urlToUse = args.getString(URL_KEY);

                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;

                    String path = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Recipe-DB";
                    /*String path = EuZinContract.MainGrid.DB_PATH;*/

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
                            return "Server returned HTTP " + connection.getResponseCode()
                                    + " " + connection.getResponseMessage();
                        }

                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        int fileLength = connection.getContentLength();

                        // download the file
                        input = connection.getInputStream();

                        /*File fToPut = new File(dir, "mainGrid.db");*/
                        File fToPut = new File(dir, "2.jpeg");

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
                        return e.toString();
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
                    return null;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
            EuZinNotification();

        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Getting the database from EuZinMainDbHelper
        try {
            dbHelper = new EuZinMainGridDbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDb = dbHelper.getWritableDatabase();

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //setting Context and column number for grid
        mGridLayoutManager = new GridLayoutManager(this, 2);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, 3);
        }
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        //Setting the adapter
        mEuZinAdapter = new EuZinMainAdapter(this, this);
        mRecyclerView.setAdapter(mEuZinAdapter);

        getSupportLoaderManager().initLoader(MAIN_LOADER, null, this);
        getSupportLoaderManager().initLoader(DATABASE_LOADER, null, mLoaderCallBackString);

        mBroadcastReceiver = new EuZinBroadcast();
        mFilter = new IntentFilter();
        mFilter.addAction(NUMBER_OF_RECEIVER);
        mFilter.addAction(DOWNLOAD_OF_RECEIVER);

        EuZinJobDispatcher.scheduleFirebaseJobDispatcherSync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_freshDB) {
            downloadFromFirebase();
            return true;
        }

        if (id == R.id.action_freshServiceDB) {
            Intent intent = new Intent(this, EuZinService.class);
            startService(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor cursor;

            @Override
            protected void onStartLoading() {

                if (cursor != null) {
                    deliverResult(cursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(EuZinContract.MainGrid.CONTENT_URI_MAIN,null,null,null,null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                /*try {
                    Cursor mCursor = mDb.query(EuZinContract.MainGrid.TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    return mCursor;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }*/
            }

            @Override
            public void deliverResult(Cursor data) {
                cursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mEuZinAdapter.setCursorData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int itemIndex) {

        Intent intent = new Intent(MainActivity.this, SunScreen.class);
        intent.putExtra(NUMBER_OF_GRID, itemIndex);
        startActivity(intent);

    }

    private void downloadFromFirebase() {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(URL_KEY, URL_TO_DOWNLOAD);

        LoaderManager loaderManager = getSupportLoaderManager();
        // COMPLETED (22) Get our Loader by calling getLoader and passing the ID we specified
        Loader<String> githubSearchLoader = loaderManager.getLoader(DATABASE_LOADER);
        // COMPLETED (23) If the Loader was null, initialize it. Else, restart it.
        if (githubSearchLoader == null) {
            loaderManager.initLoader(DATABASE_LOADER, queryBundle, mLoaderCallBackString);
        } else {
            loaderManager.restartLoader(DATABASE_LOADER, queryBundle, mLoaderCallBackString);
        }

    }

    public class EuZinBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(NUMBER_OF_RECEIVER)) {
                getSupportLoaderManager().restartLoader(MAIN_LOADER, null, MainActivity.this);
                Log.e("MainBroadcast", "Restarted");
            }else if(action.equals(DOWNLOAD_OF_RECEIVER)){
                downloadFromFirebase();
                Log.e("MainBroadcast", "Picture Downoaded");
            }
        }
    }

    private void EuZinNotification(){

        /*Bitmap largeIcon = BitmapFactory.decodeResource(
                getResources(),R.drawable.heart_out);*/

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/2.jpeg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options); //This gets the image

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.heart_in)
                .setContentTitle("Notification")
                .setContentText("Download completed")
                .setAutoCancel(true);

        Intent detailIntentForToday = new Intent(this, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }
}
