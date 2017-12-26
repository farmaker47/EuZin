package com.george.euzin.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;


public class EuZinContentProvider extends ContentProvider {

    public static final int MAIN_GRID = 100;
    public static final int MAIN_GRID_ID = 101;
    public static final int DETAILS_VITAMIN = 200;
    public static final int DETAILS_VITAMIN_ID = 201;
    public static final int DETAILS_SUNSCREEN = 300;
    public static final int DETAILS_SUNSCREEN_ID = 301;

    private EuZinMainGridDbHelper dbHelper;

    private static final UriMatcher sUriMatcher = buildUriMacher();

    public static UriMatcher buildUriMacher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_MAIN, MAIN_GRID);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_MAIN + "/#", MAIN_GRID_ID);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_VITAMIN, DETAILS_VITAMIN);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_VITAMIN + "/#", DETAILS_VITAMIN_ID);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_SUNSCREEN, DETAILS_SUNSCREEN);
        uriMatcher.addURI(EuZinContract.AUTHORITY, EuZinContract.PATH_TABLE_SUNSCREEN + "/#", DETAILS_SUNSCREEN_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        try {
            dbHelper = new EuZinMainGridDbHelper(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        final SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {

            case MAIN_GRID:
                retCursor = sqLiteDatabase.query(EuZinContract.MainGrid.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case DETAILS_VITAMIN:
                retCursor = sqLiteDatabase.query(EuZinContract.DetailView.TABLE_NAME_VITAMIN, strings, s, strings1, null, null, s1);
                break;
            case DETAILS_SUNSCREEN:
                retCursor = sqLiteDatabase.query(EuZinContract.DetailView.TABLE_NAME_SUNSCREEN, strings, s, strings1, null, null, s1);
                break;
            default:
                throw new UnsupportedOperationException("Uknown " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        final SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        int tasksUpdated;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case DETAILS_VITAMIN_ID:

                String id = uri.getPathSegments().get(1);

                tasksUpdated = sqLiteDatabase.update(EuZinContract.DetailView.TABLE_NAME_VITAMIN, contentValues, "_id=?", new String[]{id});

                break;
            case DETAILS_SUNSCREEN_ID:
                String id2 = uri.getPathSegments().get(1);

                tasksUpdated = sqLiteDatabase.update(EuZinContract.DetailView.TABLE_NAME_SUNSCREEN, contentValues, "_id=?", new String[]{id2});

                break;
            case MAIN_GRID_ID:
                String id3 = uri.getPathSegments().get(1);

                tasksUpdated = sqLiteDatabase.update(EuZinContract.MainGrid.TABLE_NAME, contentValues, "_id=?", new String[]{id3});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }
}
