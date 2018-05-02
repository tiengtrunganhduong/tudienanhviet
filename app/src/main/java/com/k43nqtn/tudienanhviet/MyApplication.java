package com.k43nqtn.tudienanhviet;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by User on 4/29/2018.
 */

public class MyApplication extends Application {
    public SQLiteDatabase wordsRdb, dictRdb, openRdb, openWdb;

//    public MyApplication() {
//        super.onCreate();
//
//        WordsDbHelper wordsDbHelper = new WordsDbHelper(this.getApplicationContext());
//        wordsRdb = wordsDbHelper.getReadableDatabase();
//
//        DictDbHelper dictDbHelper = new DictDbHelper(this.getApplicationContext());
//        dictRdb = dictDbHelper.getReadableDatabase();
//
//        OpenDbHelper openDbHelper = new OpenDbHelper(this.getApplicationContext());
//        openRdb = openDbHelper.getReadableDatabase();
//        openWdb = openDbHelper.getWritableDatabase();
//
//    }
}
