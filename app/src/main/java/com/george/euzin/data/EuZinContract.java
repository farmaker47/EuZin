package com.george.euzin.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by farmaker1 on 16/12/2017.
 */

public class EuZinContract {

    public static final String AUTHORITY = "com.george.euzin";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TABLE_SUNSCREEN = "detailTable";
    public static final String PATH_TABLE_VITAMIN = "vitaminDetailTable";
    public static final String PATH_TABLE_MAIN = "gridTable";

    public static final class MainGrid implements BaseColumns {

        public static final Uri CONTENT_URI_MAIN = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE_MAIN).build();

        public static final String TABLE_NAME = "gridTable";
        public static final String DB_PATH = "/data/data/com.george.euzin/databases/";

        public static final String GRID_IMAGE = "imageGrid";
        public static final String GRID_TEXT = "gridText";
        public static final String GRID_TEXT_ENGLISH = "gridEnglish";


    }

    public static final class DetailView implements BaseColumns {

        public static final Uri CONTENT_URI_SUNSCREEN = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE_SUNSCREEN).build();
        public static final Uri CONTENT_URI_VITAMIN = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE_VITAMIN).build();

        //Table Names
        public static final String TABLE_NAME_SUNSCREEN = "detailTable";
        public static final String TABLE_NAME_VITAMIN = "vitaminDetailTable";

        public static final String DETAIL_VIEW_IMAGE = "imageGeneral";

        public static final String DETAIL_VIEW_TITLE_TEXT = "greekTitle";
        public static final String DETAIL_VIEW_TITLE_ENGLISH = "englishTitle";

        public static final String DETAIL_VIEW_PERIGRAFI_TEXT = "perigrafiGreek";
        public static final String DETAIL_VIEW_PERIGRAFI_ENGLISH = "toEnglishPerigrafi";

        public static final String DETAIL_VIEW_HEART = "heart";

        public static final String DETAIL_VIEW_NAME_TABLE = "nameTable";
        public static final String DETAIL_VIEW_ABSOLUTE_INDEX = "absoluteIndex";
    }

}
