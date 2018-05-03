package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class OpenDbHelper extends SQLiteOpenHelper {
    OpenDbHelper(Context context) {
        super(context, OpenDbContract.DB_NAME, null, OpenDbContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OpenDbContract.MyWord.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
