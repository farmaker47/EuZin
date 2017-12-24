package com.george.euzin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.george.euzin.data.EuZinContract;
import com.george.euzin.data.EuZinMainGridDbHelper;

import java.io.IOException;

public class SunScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, EuZinDetailAdapter.euZinDetailClickItemListener {

    private RecyclerView mRecyclerView;
    private EuZinDetailAdapter mEuZinAdapter;
    private LinearLayoutManager mLayoutManager;
    private SQLiteDatabase mDb;
    private EuZinMainGridDbHelper dbHelper;
    private static final int MAIN_LOADER = 47;
    private MainActivity mMain;
    private Cursor mCursorAfterClick;
    public static final String NUMBER_OF_LIST = "listNumber";
    private static final String TABLE_TO_PASS = "table_pass";

    private int numberOfIncoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra(mMain.NUMBER_OF_GRID)) {
            numberOfIncoming = intent.getIntExtra(mMain.NUMBER_OF_GRID, 0);
            Log.e("DetailActivity", String.valueOf(numberOfIncoming));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Getting the database from EuZinMainDbHelper
        try {
            dbHelper = new EuZinMainGridDbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDb = dbHelper.getWritableDatabase();

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.detailRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Setting the adapter
        mEuZinAdapter = new EuZinDetailAdapter(this, this);
        mRecyclerView.setAdapter(mEuZinAdapter);

        getSupportLoaderManager().initLoader(MAIN_LOADER, null, this);

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

                if (numberOfIncoming == 2) {
                    try {
                        /*Cursor mCursor = mDb.query(EuZinContract.DetailView.TABLE_NAME_SUNSCREEN,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);

                        Log.e("AFTERquery",EuZinContract.DetailView.TABLE_NAME_SUNSCREEN);*/

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SunScreen.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TABLE_TO_PASS, EuZinContract.DetailView.TABLE_NAME_SUNSCREEN);
                        editor.apply();

                        try {

                            return getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_SUNSCREEN,null,null,null,null);

                            /*Cursor cursor1 = getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_SUNSCREEN, null, null, null, null);
                            Cursor cursor2 = getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_VITAMIN, null, null, null, null);

                            Cursor mergedCursor = new MergeCursor(new Cursor[]{cursor1, cursor2});

                            if (mergedCursor.getCount() < 0) {
                                return null;
                            }

                            mergedCursor.moveToFirst();

                            return mergedCursor;*/
                            /*return getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_SUNSCREEN,null,null,null,null);*/
                            /*return getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_SUNSCREEN,null,"heart=?",new String[]{"1"},null);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else if (numberOfIncoming == 1) {
                    try {
                        /*Cursor mCursor = mDb.query(EuZinContract.DetailView.TABLE_NAME_VITAMIN,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
                        Log.e("AFTERquery",EuZinContract.DetailView.TABLE_NAME_VITAMIN);*/

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SunScreen.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TABLE_TO_PASS, EuZinContract.DetailView.TABLE_NAME_VITAMIN);
                        editor.apply();

                        try {
                            return getContentResolver().query(EuZinContract.DetailView.CONTENT_URI_VITAMIN, null, null, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
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
        if (data != null) {
            mEuZinAdapter.setCursorData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int itemIndex) {

        Intent intent = new Intent(SunScreen.this, ObservableActivity.class);
        intent.putExtra(NUMBER_OF_LIST, itemIndex);
        startActivity(intent);

    }
}
