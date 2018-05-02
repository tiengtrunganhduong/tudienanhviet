package com.k43nqtn.tudienanhviet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 4/6/2018.
 */

class LookupCountUpdateTask extends AsyncTask<Void, Void, Void> {
    Context context;
    private String word, dir;
    private SQLiteDatabase rdb, wdb;
    private TextView tvLookupCount;
    private String lookupCountAlertMsg;

    LookupCountUpdateTask(Context context,
                          String word,
                          String dir,
                          SQLiteDatabase rdb,
                          SQLiteDatabase wdb,
                          TextView tvLookupCount
    ) {
        this.context = context;
        this.word = word;
        this.dir = dir;
        this.rdb = rdb;
        this.wdb = wdb;
        this.tvLookupCount = tvLookupCount;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Cursor cursor = rdb.rawQuery(
                "SELECT "
                        + OpenDbContract.MyWord.COL_ID + ", "
                        + OpenDbContract.MyWord.COL_LOOKUP_COUNT
                        + " FROM " + OpenDbContract.MyWord.TBL_NAME
                        + " WHERE " + OpenDbContract.MyWord.COL_WORD + " LIKE @word"
                        + " AND " + OpenDbContract.MyWord.COL_DIR + " = @dir",
                new String[]{ word, dir }
        );
        if (cursor != null) {
            cursor.moveToFirst();
            String lastLookupTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());

            if (cursor.getCount() > 0) {
                int lookedUpCount = cursor.getInt(cursor.getColumnIndex(OpenDbContract.MyWord.COL_LOOKUP_COUNT));
                int id = cursor.getInt(cursor.getColumnIndex(OpenDbContract.MyWord.COL_ID));
                Cursor updater = wdb.rawQuery(
                        " UPDATE "
                        + OpenDbContract.MyWord.TBL_NAME
                        + " SET "
                        + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME + " = '" + lastLookupTime + "', "
                        + OpenDbContract.MyWord.COL_LOOKUP_COUNT + " = (1 + " + OpenDbContract.MyWord.COL_LOOKUP_COUNT + ")"
                        + " WHERE " + OpenDbContract.MyWord.COL_ID + " = @id"
                        ,
                        new String[]{ String.valueOf(id) }
                );
                updater.moveToFirst();
                updater.close();

                lookupCountAlertMsg = String.format(
                        context.getResources().getString(R.string.lookup_count_message),
                        lookedUpCount
                );
            } else {
                ContentValues values = new ContentValues();
                values.put(OpenDbContract.MyWord.COL_WORD, word);
                values.put(OpenDbContract.MyWord.COL_DIR, dir);
                values.put(OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME, lastLookupTime);
                values.put(OpenDbContract.MyWord.COL_LOOKUP_COUNT, 1);
                values.put(OpenDbContract.MyWord.COL_FAVOURITE, 0);
                wdb.insert(OpenDbContract.MyWord.TBL_NAME, null, values);
            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (lookupCountAlertMsg != null) {
            tvLookupCount.setVisibility(TextView.VISIBLE);
            tvLookupCount.setText(lookupCountAlertMsg);
        }
    }

}
