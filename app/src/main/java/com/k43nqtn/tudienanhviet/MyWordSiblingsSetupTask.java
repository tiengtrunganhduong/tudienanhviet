package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;


class MyWordSiblingsSetupTask extends AsyncTask<Void, Void, Void> {
    Context context;
    private String word, lastLookupTime;
    private SQLiteDatabase rdb;
    private Button prevButton, nextButton;
    private String prevWord, prevLastLookupTime, prevDir;
    private String nextWord, nextLastLookupTime, nextDir;

    MyWordSiblingsSetupTask(Context context,
                            String word,
                            String lastLookupTime,
                            SQLiteDatabase rdb,
                            Button prevButton,
                            Button nextButton
    ) {
        this.context = context;
        this.word = word;
        this.lastLookupTime = lastLookupTime;
        this.rdb = rdb;
        this.prevButton = prevButton;
        this.nextButton = nextButton;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // PREV
        Cursor prevCursor = rdb.rawQuery(
                "SELECT "
                        + OpenDbContract.MyWord.COL_WORD + ", "
                        + OpenDbContract.MyWord.COL_DIR + ", "
                        + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME
                        + " FROM " + OpenDbContract.MyWord.TBL_NAME
                        + " WHERE " + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME + " < @lastLookupTime"
                        + " AND "+ OpenDbContract.MyWord.COL_WORD + " NOT LIKE @word"
                        + " ORDER BY " + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME
                        + " DESC LIMIT 1",
                new String[] {lastLookupTime, word}
        );

        if (prevCursor != null) {
            prevCursor.moveToFirst();
            if (prevCursor.getCount() > 0) {
                prevWord = prevCursor.getString(prevCursor.getColumnIndex(OpenDbContract.MyWord.COL_WORD));
                prevDir = prevCursor.getString(prevCursor.getColumnIndex(OpenDbContract.MyWord.COL_DIR));
                prevLastLookupTime = prevCursor.getString(prevCursor.getColumnIndex(OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME));
            }
            prevCursor.close();
        }

        // NEXT
        Cursor nextCursor = rdb.rawQuery(
                "SELECT * FROM " + OpenDbContract.MyWord.TBL_NAME
                        + " WHERE " + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME + " > @lastLookupTime"
                        + " AND " + OpenDbContract.MyWord.COL_WORD + " NOT LIKE @word"
                        + " ORDER BY " + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME
                        + " ASC LIMIT 1",
                new String[] {lastLookupTime, word}
        );

        if (nextCursor != null) {
            nextCursor.moveToFirst();
            if (nextCursor.getCount() > 0) {
                nextWord = nextCursor.getString(nextCursor.getColumnIndex(OpenDbContract.MyWord.COL_WORD));
                nextDir = nextCursor.getString(nextCursor.getColumnIndex(OpenDbContract.MyWord.COL_DIR));
                nextLastLookupTime = nextCursor.getString(nextCursor.getColumnIndex(OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME));
            }
            nextCursor.close();
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
        
        setupButton(prevButton, prevWord, prevLastLookupTime, prevDir);
        setupButton(nextButton, nextWord, nextLastLookupTime, nextDir);
    }
    
    private void setupButton(Button button, final String word, final String lastLookupTime, final String dir) {
        if (button != null && word != null && lastLookupTime != null && dir != null) {
            button.setVisibility(View.VISIBLE);
            button.setText(word);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, WordActivity.class);

                    intent.putExtra("word", word);
                    intent.putExtra("last_lookup_time", lastLookupTime);
                    intent.putExtra("dir", dir);

                    context.startActivity(intent);
                }
            });
        }
    }
}
