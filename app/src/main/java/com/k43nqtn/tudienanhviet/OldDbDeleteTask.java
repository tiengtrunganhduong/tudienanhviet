package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;

/**
 * Created by User on 4/29/2018.
 */

class OldDbDeleteTask extends AsyncTask<Void, Void, Void> {
    Context context;
    private String dbName;

    OldDbDeleteTask(Context context, String dbName) {
        this.context = context;
        this.dbName = dbName;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            File dbFile = context.getDatabasePath(dbName);
            if (dbFile.exists()) {
                dbFile.delete();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }
}
