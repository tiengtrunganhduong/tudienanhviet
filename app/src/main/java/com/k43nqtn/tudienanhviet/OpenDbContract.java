package com.k43nqtn.tudienanhviet;

import android.provider.BaseColumns;

/**
 * Created by User on 3/13/2018.
 */

final class OpenDbContract {
    static final String DB_NAME = "open.db";
    static final int DB_VERSION = 1;

    static class MyWord implements BaseColumns {
        static final String TBL_NAME = "my_word";
        static final String COL_ID = _ID;
        static final String COL_WORD = "word";
        static final String COL_DIR = "dir";
        static final String COL_LAST_LOOKUP_TIME = "last_lookup_time";
        static final String COL_LOOKUP_COUNT = "lookup_count";
        static final String COL_FAVOURITE = "favourite";

        static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                TBL_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_WORD + " TEXT NOT NULL UNIQUE," +
                COL_DIR + " TEXT NOT NULL," +
                COL_LAST_LOOKUP_TIME + " TEXT," +
                COL_LOOKUP_COUNT + " INTEGER," +
                COL_FAVOURITE + " INTEGER)";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
    }
}
