package com.george.euzin.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class EuZinMainGridDbHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String DB_NAME = "mainGrid.db";
    private static final int DB_VERSION = 2;
    private SQLiteDatabase mDb;

    public EuZinMainGridDbHelper(Context context) throws IOException {
        super(context, DB_NAME, null, DB_VERSION);

        this.mContext = context;
        boolean dbexist = checkdatabase();
        if (dbexist) {
            opendatabase();
        } else {
            createdatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String DATABASE_CREATE_MAIN =
                "CREATE TABLE IF NOT EXISTS " + EuZinContract.MainGrid.TABLE_NAME + "(" +
                        EuZinContract.MainGrid._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EuZinContract.MainGrid.GRID_TEXT + " TEXT NOT NULL, " +
                        EuZinContract.MainGrid.GRID_TEXT_ENGLISH + " TEXT NOT NULL, " +
                        EuZinContract.MainGrid.GRID_IMAGE + " BLOB " +
                        ");";
        String DATABASE_CREATE_DETAIL =
                "CREATE TABLE IF NOT EXISTS " + EuZinContract.DetailView.TABLE_NAME + "(" +
                        EuZinContract.DetailView._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EuZinContract.DetailView.DETAIL_VIEW_TITLE_TEXT + " TEXT NOT NULL, " +
                        EuZinContract.DetailView.DETAIL_VIEW_TITLE_ENGLISH + " TEXT NOT NULL, " +
                        EuZinContract.DetailView.DETAIL_VIEW_PERIGRAFI_TEXT + " TEXT NOT NULL, " +
                        EuZinContract.DetailView.DETAIL_VIEW_PERIGRAFI_ENGLISH + " TEXT NOT NULL, " +
                        EuZinContract.DetailView.DETAIL_VIEW_IMAGE + " BLOB " +
                        ");";

        sqLiteDatabase.execSQL(DATABASE_CREATE_MAIN);
        sqLiteDatabase.execSQL(DATABASE_CREATE_DETAIL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EuZinContract.MainGrid.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EuZinContract.DetailView.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private boolean checkdatabase() {

        boolean checkdb = false;
        try {
            String myPath = EuZinContract.MainGrid.DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void opendatabase() throws SQLException {
        //Open the database
        String mypath = EuZinContract.MainGrid.DB_PATH + DB_NAME;
        mDb = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READONLY);

    }

    private void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if (dbexist) {
            System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }


    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = EuZinContract.MainGrid.DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

}
