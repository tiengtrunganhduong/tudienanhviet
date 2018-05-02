package com.k43nqtn.tudienanhviet;

import android.content.Context;

/**
 * Created by User on 4/17/2018.
 */

class DictDbHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = DictDbContract.DATABASE_NAME;
    private static final int DATABASE_VERSION = DictDbContract.DATABASE_VERSION;

    DictDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
