package com.george.euzin.data;

import android.provider.BaseColumns;

/**
 * Created by farmaker1 on 16/12/2017.
 */

public class EuZinContract {

    public static final class MainGrid implements BaseColumns{

        public static final String TABLE_NAME = "gridTable";
        public static final String DB_PATH = "/data/data/com.george.euzin/databases/";

        public  static final String GRID_IMAGE = "imageGrid";
        public  static final String GRID_TEXT = "gridText";
        public  static final String GRID_TEXT_ENGLISH = "gridEnglish";


    }

    public static final class DetailView implements BaseColumns{

        //Table Names
        public static final String TABLE_NAME_SUNSCREEN = "detailTable";
        public static final String TABLE_NAME_VITAMIN = "vitaminDetailTable";

        public  static final String DETAIL_VIEW_IMAGE = "imageGeneral";

        public  static final String DETAIL_VIEW_TITLE_TEXT = "greekTitle";
        public  static final String DETAIL_VIEW_TITLE_ENGLISH = "englishTitle";

        public  static final String DETAIL_VIEW_PERIGRAFI_TEXT = "perigrafiGreek";
        public  static final String DETAIL_VIEW_PERIGRAFI_ENGLISH = "toEnglishPerigrafi";

        public  static final String DETAIL_VIEW_HEART = "heart";
    }

}
