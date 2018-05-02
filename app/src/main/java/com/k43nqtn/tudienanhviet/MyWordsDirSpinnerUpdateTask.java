package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by User on 4/29/2018.
 */

class MyWordsDirSpinnerUpdateTask extends AsyncTask<Void, Void, ArrayAdapter> {
    Context context;
    private SQLiteDatabase rdb;
    private Spinner spinner;
    private int my_words_dir;


    MyWordsDirSpinnerUpdateTask(Context context, SQLiteDatabase rdb, Spinner spinner, int my_words_dir) {
        this.context = context;
        this.rdb = rdb;
        this.spinner = spinner;
        this.my_words_dir = my_words_dir;
    }

    @Override
    protected ArrayAdapter<String> doInBackground(Void... voids) {
        String[] dirs = {
                "All (%d)",
                "E-V (%d)",
                "V-E (%d)",
        };

        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = String.format(dirs[i], getMyWordsCount(rdb, i));
        }

        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context, R.layout.small_spinner_item, dirs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    @Override
    protected void onPostExecute(ArrayAdapter adapter) {
        super.onPostExecute(adapter);

        spinner.setAdapter(adapter);
        spinner.setSelection(my_words_dir);
    }

    private int getMyWordsCount(SQLiteDatabase rdb, int dir_int) {
        String where_clause;

        switch (dir_int) {
            case 1:
                where_clause = " WHERE " + OpenDbContract.MyWord.COL_DIR + " = 'ev'";
                break;
            case 2:
                where_clause = " WHERE " + OpenDbContract.MyWord.COL_DIR + " = 've'";
                break;
            case 0:
            default:
                where_clause = "";
        }

        final Cursor counter = rdb.rawQuery(
                "SELECT COUNT(*) FROM "
                        + OpenDbContract.MyWord.TBL_NAME
                        + where_clause
                , null
        );

        int result = 0;

        if (counter != null) {
            counter.moveToFirst();
            result = counter.getInt(0);
            counter.close();
        }

        return result;
    }
}
