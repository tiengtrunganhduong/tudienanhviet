package com.k43nqtn.tudienanhviet;

import android.provider.BaseColumns;


final class WordsDbContract {
    static final String DATABASE_NAME = "words.db";
    static final int DATABASE_VERSION = 2;

    static final String COLUMN_ID = BaseColumns._ID;
    static final String COLUMN_LANG = "lang";
    static final String COLUMN_LOWERCASE = "lowercase";
    static final String COLUMN_ACCENTLESS = "accentless";
    static final String COLUMN_TITLE = "title";

//    public static final String TABLE_0 = "words_0";
//    public static final String TABLE_1 = "words_1";
//    public static final String TABLE_2 = "words_2";
//    public static final String TABLE_3 = "words_3";
//    public static final String TABLE_4 = "words_4";
//    public static final String TABLE_5 = "words_5";
//    public static final String TABLE_6 = "words_6";
//    public static final String TABLE_7 = "words_7";
//    public static final String TABLE_8 = "words_8";
//    public static final String TABLE_9 = "words_9";
//    public static final String TABLE_10 = "words_10";
//    public static final String TABLE_11 = "words_11";
//    public static final String TABLE_12 = "words_12";
//    public static final String TABLE_13 = "words_13";
//    public static final String TABLE_14 = "words_14";
//    public static final String TABLE_15 = "words_15";
//    public static final String TABLE_16 = "words_16";
//    public static final String TABLE_17 = "words_17";
//    public static final String TABLE_18 = "words_18";
//    public static final String TABLE_19 = "words_19";
//    public static final String TABLE_20 = "words_20";
//    public static final String TABLE_21 = "words_21";
//    public static final String TABLE_22 = "words_22";
//    public static final String TABLE_23 = "words_23";
//    public static final String TABLE_24 = "words_24";
//    public static final String TABLE_25 = "words_25";
//    public static final String TABLE_26 = "words_26";

    static String getTableName(int index) {
        return "words_" + index;
    }
}
