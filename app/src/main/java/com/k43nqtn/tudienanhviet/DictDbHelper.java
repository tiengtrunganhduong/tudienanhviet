package com.k43nqtn.tudienanhviet;

import android.content.Context;

class DictDbHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = DictDbContract.DATABASE_NAME;
    private static final int DATABASE_VERSION = DictDbContract.DATABASE_VERSION;

    DictDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}
