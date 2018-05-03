package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by User on 4/6/2018.
 */

class SpeechButtonsSetupTask extends AsyncTask<Void, Void, Void> {
    Context context;
    private HorizontalScrollView speechButtonsScroller;
    private LinearLayout speechButtonsContainer;
    String word;
    private String lang;
    private SQLiteDatabase rdb;
    private String encoded_word;
    private String[] pron_paths;
    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;
    private Values values;

    SpeechButtonsSetupTask(Context context,
                           String word,
                           String lang,
                           SQLiteDatabase rdb,
                           HorizontalScrollView speechButtonsScroller,
                           LinearLayout speechButtonsContainer
    ) {
        this.context = context;
        this.word = word;
        this.lang = lang;
        this.rdb = rdb;
        this.speechButtonsScroller = speechButtonsScroller;
        this.speechButtonsContainer = speechButtonsContainer;

        this.values = new Values(context);

        mediaPlayer = new MediaPlayer();

    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (lang != null && lang.compareTo("en") == 0) {
            Cursor cursor = rdb.rawQuery(
                    " SELECT "
                    + DictDbContract.EnPronTable.COLUMN_NAME_ENCODED + ", "
                    + DictDbContract.EnPronTable.COLUMN_NAME_PATHS
                    + " FROM " + DictDbContract.EnPronTable.TABLE_NAME
                    + " WHERE " + DictDbContract.EnPronTable.COLUMN_NAME_WORD + " LIKE @word"
                    + " LIMIT 1",
                    new String[]{ word }
            );

            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    encoded_word = cursor.getString(cursor.getColumnIndex(
                            DictDbContract.EnPronTable.COLUMN_NAME_ENCODED
                    ));

                    String paths_str = cursor.getString(cursor.getColumnIndex(
                            DictDbContract.EnPronTable.COLUMN_NAME_PATHS
                    ));
                    pron_paths = paths_str.split("\n");
                }
                cursor.close();
            }
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


        if (lang != null && lang.compareTo("en") == 0) {
            speechButtonsScroller.setVisibility(View.VISIBLE);

            final Drawable ic_speaker = ContextCompat.getDrawable(context, R.drawable.ic_speaker);

            final Button autoSpeechButton = new Button(context);
            autoSpeechButton.setText("AUTO");
            setupSpeechButtonStyle(autoSpeechButton);
            autoSpeechButton.setCompoundDrawablesWithIntrinsicBounds(ic_speaker, null, null, null);

            speechButtonsContainer.addView(autoSpeechButton);

//            final Button ukTtsButton = new Button(context);
//            ukTtsButton.setCompoundDrawablesWithIntrinsicBounds(ic_speaker, null, null, null);
//            ukTtsButton.setText("UK");
//            ukTtsButton.setLayoutParams(speechBtnLayoutParams);
//            speechButtonsContainer.addView(ukTtsButton);

//            final Button usTtsButton = new Button(context);
//            usTtsButton.setCompoundDrawablesWithIntrinsicBounds(ic_speaker, null, null, null);
//            usTtsButton.setText("US");
//            usTtsButton.setLayoutParams(speechBtnLayoutParams);
//            speechButtonsContainer.addView(usTtsButton, 1);

            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {

                        autoSpeechButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        });

//                        ukTtsButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                tts.setLanguage(Locale.UK);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
//                                } else {
//                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
//                                }
//                            }
//                        });

//                        usTtsButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                tts.setLanguage(Locale.US);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
//                                } else {
//                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
//                                }
//                            }
//                        });
                    } else {

                        autoSpeechButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert(R.string.no_tts_title, R.string.no_tts_message);
                            }
                        });

//                        ukTtsButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alert(R.string.no_tts_title, R.string.no_tts_message);
//                            }
//                        });

//                        usTtsButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alert(R.string.no_tts_title, R.string.no_tts_message);
//                            }
//                        });
                    }
                }
            });

            if (encoded_word != null && pron_paths.length > 0) {
                final Drawable ic_play = ContextCompat.getDrawable(context, R.drawable.ic_play);
                final Drawable ic_play_pending = ContextCompat.getDrawable(context, R.drawable.ic_play_pending);
                final Drawable ic_play_done = ContextCompat.getDrawable(context, R.drawable.ic_play_done);
                final Drawable ic_play_error = ContextCompat.getDrawable(context, R.drawable.ic_play_error);

                String p1 = encoded_word.substring(0, Math.min(1, encoded_word.length()));
                String p3 = encoded_word.substring(0, Math.min(3, encoded_word.length()));
                String p5 = encoded_word.substring(0, Math.min(5, encoded_word.length()));

                final ArrayList<Button> speechBtnList = new ArrayList<>();

                for (String path: pron_paths) {

                    String[] path_parts = path.split("/");
                    if (path_parts.length != 2) {
                        continue;
                    }

                    final String uri = String.format(
                            "http://horoquiz.com/pron/en/%s/%s/%s/%s/%s.mp3",
                            path, p1, p3, p5, encoded_word
                    );

                    final Button speechBtn = new Button(context);
                    speechBtnList.add(speechBtn);
                    speechBtn.setCompoundDrawablesWithIntrinsicBounds(ic_play, null, null, null);
                    setupSpeechButtonStyle(speechBtn);
                    switch (path_parts[1]) {
                        case "1":
                            speechBtn.setText(context.getResources().getString(R.string.pron_uk));
                            break;
                        case "2":
                            speechBtn.setText(context.getResources().getString(R.string.pron_us));
                            break;
                        case "0":
                        default:
                            speechBtn.setText(context.getResources().getString(R.string.pron_unknown));
                            break;
                    }
                    speechBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isNetworkAvailable()) {

                                for (Button _speechBtn : speechBtnList) {
                                    if (!_speechBtn.isClickable()) {
                                        _speechBtn.setCompoundDrawablesWithIntrinsicBounds(ic_play, null, null, null);
                                        _speechBtn.setClickable(true);
                                    }
                                }

                                speechBtn.setCompoundDrawablesWithIntrinsicBounds(ic_play_pending, null, null, null);
                                speechBtn.setClickable(false);

                                try {
                                    mediaPlayer.release();
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(uri);
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            mediaPlayer.start();
                                            speechBtn.setCompoundDrawablesWithIntrinsicBounds(ic_play_done, null, null, null);
                                            speechBtn.setClickable(true);
                                        }
                                    });
                                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                            speechBtn.setCompoundDrawablesWithIntrinsicBounds(ic_play_error, null, null, null);
                                            speechBtn.setClickable(true);
                                            return false;
                                        }
                                    });
                                    mediaPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                alert(R.string.pronunciation_no_internet_connection_title,
                                        R.string.pronunciation_no_internet_connection_message);
                            }
                        }
                    });
                    speechButtonsContainer.addView(speechBtn);

                }
            }
        }
    }

    private void setupSpeechButtonStyle(Button btn) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, values.DP_3, 0);
        btn.setLayoutParams(layoutParams);
        btn.setMinWidth(0);
        btn.setMinHeight(0);
        btn.setMinimumWidth(0);
        btn.setMinimumHeight(0);
        btn.setTextSize(13);
        btn.setCompoundDrawablePadding(values.DP_5);
        btn.setPadding(values.DP_13, values.DP_12, values.DP_13, values.DP_12);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void alert(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
