package com.george.euzin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.euzin.data.EuZinContract;
import com.george.euzin.data.EuZinMainGridDbHelper;

import java.io.IOException;

public class ObservableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SunScreen mSun;
    private int number;
    private ImageView imageO;
    private TextView textO;
    private static final String TABLE_TO_PASS = "table_pass";
    private String tableToQuery;
    private static final int MAIN_LOADER = 74;

    private SQLiteDatabase mDb;
    private EuZinMainGridDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observable);

        Intent intent = getIntent();
        if (intent.hasExtra(mSun.NUMBER_OF_LIST)) {
            number = intent.getIntExtra(mSun.NUMBER_OF_LIST, 0);
            Log.e("ObserveActivity", String.valueOf(number));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tableToQuery = sharedPreferences.getString(TABLE_TO_PASS, "detailTable");
        Log.e("ObserveActivityTABLE", tableToQuery);

        imageO = (ImageView) findViewById(R.id.imageObservable);
        textO = (TextView) findViewById(R.id.textViewObservable);

        //Getting the database from EuZinMainDbHelper
        try {
            dbHelper = new EuZinMainGridDbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDb = dbHelper.getReadableDatabase();

        getSupportLoaderManager().initLoader(MAIN_LOADER,null,this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                    Cursor mCursor = mDb.query(tableToQuery,
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
                }
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
        data.moveToPosition(number);
        String text = data.getString(data.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_TITLE_TEXT));

        byte[] image = data.getBlob(data.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_IMAGE));
        Bitmap bitmap = getImage(image);
        imageO.setImageBitmap(bitmap);
        textO.setText(text);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
