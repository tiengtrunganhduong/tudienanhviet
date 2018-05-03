package com.k43nqtn.tudienanhviet;

import android.content.Context;

class WordsDbHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = WordsDbContract.DATABASE_NAME;
    private static final int DATABASE_VERSION = WordsDbContract.DATABASE_VERSION;

    WordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}
