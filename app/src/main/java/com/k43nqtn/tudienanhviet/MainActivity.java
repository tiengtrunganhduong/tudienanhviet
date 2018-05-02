package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.facebook.ads.AdSize;

public class MainActivity extends AppCompatActivity {

    Context context;
    MyApplication myApplication;
    CursorAdapter cursorAdapter;
    CursorAdapter myWordsAdapter;
    SearchView searchView;
    ListView list_suggestions;
    ListView list_my_words;
    LinearLayout my_words_heading;
    Spinner my_words_sort_spinner;
    Spinner my_words_dir_spinner;
    int my_words_sort_order = 0;
    int my_words_dir = 0;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        myApplication = (MyApplication) this.getApplication();

        my_words_heading = (LinearLayout) findViewById(R.id.my_words_heading);
        list_suggestions = (ListView) findViewById(R.id.list_suggestions);
        list_my_words = (ListView) findViewById(R.id.list_my_words);
        my_words_sort_spinner = (Spinner) findViewById(R.id.my_words_sort_spinner);
        my_words_dir_spinner = (Spinner) findViewById(R.id.my_words_dir_spinner);


        initDatabases();

        setupSuggestionsList();

        setupMyWordsList();

        setupMyWordsSortSpinner();

        updateMyWordsDirSpinnerAsync();

        setupSearchFocusClearing();

        initAds();
    }

    private void initDatabases() {
        if (myApplication.dictRdb == null) {
            myApplication.dictRdb = (new DictDbHelper(this)).getReadableDatabase();
        }
        if (myApplication.wordsRdb == null) {
            myApplication.wordsRdb = (new WordsDbHelper(this)).getReadableDatabase();
        }
        if (myApplication.openRdb == null || myApplication.openWdb == null) {
            OpenDbHelper openDbHelper = new OpenDbHelper(this);
            myApplication.openRdb = openDbHelper.getReadableDatabase();
            myApplication.openWdb = openDbHelper.getWritableDatabase();
        }
    }

    private void setupSuggestionsList() {
        final String letters = "abcdefghijklmnopqrstuvwxyz#";
        final String a_based = "àáạảãâầấậẩẫăằắặẳẵ";
        final String e_based = "èéẹẻẽêềếệểễ";
        final String i_based = "ìíịỉĩ";
        final String o_based = "òóọỏõôồốộổỗơờớợởỡ";
        final String u_based = "ùúụủũưừứựửữ";
        final String y_based = "ỳýỵỷỹ";
        final String d_based = "đ";

        cursorAdapter = new SuggestionCursorAdapter(this, null);
        cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String search = constraint.toString().toLowerCase();
                Cursor cursor;
                if (search.compareTo("") == 0) {
                    cursor = null;
                } else {
                    char char0 = search.charAt(0);
                    if (letters.indexOf(char0) == -1) {
                        if (a_based.indexOf(char0) > -1) {
                            char0 = 'a';
                        } else if (e_based.indexOf(char0) > -1) {
                            char0 = 'e';
                        } else if (i_based.indexOf(char0) > -1) {
                            char0 = 'i';
                        } else if (o_based.indexOf(char0) > -1) {
                            char0 = 'o';
                        } else if (u_based.indexOf(char0) > -1) {
                            char0 = 'u';
                        } else if (y_based.indexOf(char0) > -1) {
                            char0 = 'y';
                        } else if (d_based.indexOf(char0) > -1) {
                            char0 = 'd';
                        } else {
                            char0 = '#';
                        }
                    }
                    int tableIndex = letters.indexOf(char0);
                    String tableName = WordsDbContract.getTableName(tableIndex);
                    cursor = myApplication.wordsRdb.rawQuery(
                            " SELECT "
                                    + WordsDbContract.COLUMN_ID + ", "
                                    + WordsDbContract.COLUMN_LANG + ", "
                                    + WordsDbContract.COLUMN_LOWERCASE + ", "
                                    + WordsDbContract.COLUMN_ACCENTLESS + ", "
                                    + WordsDbContract.COLUMN_TITLE
                                    + " FROM " + tableName
                                    + " WHERE " + WordsDbContract.COLUMN_LOWERCASE + " LIKE @search"
                                    + " OR " + WordsDbContract.COLUMN_ACCENTLESS + " LIKE @search"
                                    + " LIMIT 100"
                            , new String[]{search + "%"}
                    );
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                return cursor;
            }
        });

//            cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
//                public Cursor runQuery(CharSequence constraint) {
//                    String search = constraint.toString().trim();
//                    Cursor cursor;
//                    if (search.compareTo("") == 0) {
//                        cursor = null;
//                    } else {
//                        cursor = rdb.rawQuery(
//                                ""
//                                + " SELECT * FROM ("
//                                + " SELECT"
//                                + " _id, " + ev_word + " AS c_word, 'ev' AS " + c_dir
//                                + " FROM " + ev_tbl
//                                + " WHERE " + ev_word + " LIKE @search"
//                                + " UNION"
//                                + " SELECT"
//                                + " _id, " + ev_word + " AS c_word, 'ev' AS " + c_dir
//                                + " FROM " + ev_tbl_2
//                                + " WHERE " + ev_word + " LIKE @search"
//                                + " ) GROUP BY c_word COLLATE NOCASE"
//                                + " UNION All"
//                                + " SELECT * FROM ("
//
//                                + " SELECT"
//                                        + " _id, " + ve_word + " AS c_word, 've' AS " + c_dir
//                                        + " FROM " + ve_tbl
//                                        + " WHERE (" + ve_word + " LIKE @search)"
//                                        + " OR (" + ve_accentless + " LIKE @search)"
//                                + " UNION"
//                                + " SELECT"
//                                        + " (222000 + _id) AS _id, " + ve_word + " AS c_word, 've' AS " + c_dir
//                                        + " FROM " + ve_tbl_2
//                                        + " WHERE (" + ve_word + " LIKE @search)"
//                                        + " OR (" + ve_accentless + " LIKE @search)"
//                                + " UNION"
//                                + " SELECT"
//                                        + " (444000 + _id) AS _id, " + ve_word + " AS c_word, 've' AS " + c_dir
//                                        + " FROM " + ve_tbl_3
//                                        + " WHERE (" + ve_word + " LIKE @search)"
//                                        + " OR (" + ve_accentless + " LIKE @search)"
//                                + " ) GROUP BY c_word COLLATE NOCASE"
//                                + " ORDER BY c_word COLLATE NOCASE ASC"
//                                + " LIMIT 100"
//                                , new String[]{ search + "%" }
//
//                                // here, `=` is case sensitive, while `LIKE` is insensitive
//                        );
//                    }
//                    if (cursor != null) {
//                        cursor.moveToFirst();
//                    }
//                    return cursor;
//                }
//            });

        list_suggestions.setAdapter(cursorAdapter);

        list_suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), WordActivity.class);

                Cursor cursor = (Cursor) cursorAdapter.getItem(i);

                int lang = cursor.getInt(cursor.getColumnIndex(WordsDbContract.COLUMN_LANG));
                String dir = 0 == lang ? "ev" : "ve";
                String word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_TITLE));
                if (word == null || word.compareTo("") == 0) {
                    word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_LOWERCASE));
                }
                intent.putExtra("dir", dir);
                intent.putExtra("word", word);
                intent.putExtra("last_lookup_time", "NOW");

                startActivity(intent);
            }
        });
    }

    private void setupMyWordsList() {
        myWordsAdapter = new MyWordCursorAdapter(this, null);
        myWordsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {

                String where_clause;
                switch (my_words_dir) {
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

                String sort_clause;
                switch (my_words_sort_order) {
                    case 1:
                        sort_clause = " ORDER BY " + OpenDbContract.MyWord.COL_WORD + " COLLATE NOCASE ASC";
                        break;
                    case 0:
                    default:
                        sort_clause = " ORDER BY " + OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME + " DESC";
                }

                Cursor cursor = myApplication.openRdb.rawQuery(
                        "SELECT * FROM "
                                + OpenDbContract.MyWord.TBL_NAME
                                + where_clause
                                + sort_clause
                        , null
                );

                if (cursor != null) {
                    cursor.moveToFirst();
                }

                return cursor;
            }
        });

        list_my_words.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) myWordsAdapter.getItem(i);
                String dir = cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_DIR));
                String last_lookup_time = cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_LAST_LOOKUP_TIME));
                String word = cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_WORD));

                Intent intent = new Intent(getApplicationContext(), WordActivity.class);
                intent.putExtra("dir", dir);
                intent.putExtra("word", word);
                intent.putExtra("last_lookup_time", last_lookup_time);

                startActivity(intent);
            }
        });
        list_my_words.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) myWordsAdapter.getItem(i);
                final int id = cursor.getInt(cursor.getColumnIndex(OpenDbContract.MyWord._ID));
                final String word = cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_WORD));


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(String.format(getResources().getString(R.string.delete_confirm_title), word));
                builder.setMessage(String.format(getResources().getString(R.string.delete_confirm_message), word));
                builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String table = OpenDbContract.MyWord.TBL_NAME;
                        String whereClause = OpenDbContract.MyWord.COL_ID + " = ?";
                        String[] whereArgs = new String[]{ String.valueOf(id) };
                        if (myApplication.openWdb.delete(table, whereClause, whereArgs) > 0) {
                            Toast.makeText(
                                    context,
                                    String.format(context.getResources().getString(R.string.deleted_word), word),
                                    Toast.LENGTH_SHORT
                            ).show();
                            updateMyWordsDirSpinnerAsync();
                            myWordsAdapter.getFilter().filter("");
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F03333"));
                    }
                });
                dialog.show();

                return false;
            }
        });

        list_my_words.setAdapter(myWordsAdapter);
        myWordsAdapter.getFilter().filter("");

        my_words_sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                my_words_sort_order = position;
                myWordsAdapter.getFilter().filter("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        my_words_dir_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                my_words_dir = position;
                myWordsAdapter.getFilter().filter("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupSearchFocusClearing() {
        // on touch listener
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (searchView != null) {
                    searchView.clearFocus();
                }
                return false;
            }
        };
        list_suggestions.setOnTouchListener(onTouchListener);
        list_my_words.setOnTouchListener(onTouchListener);
    }

    private void initAds() {

        // Instantiate an AdView view
        adView = new AdView(this, "591787627837331_591787964503964", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        final LinearLayout adContainer = (LinearLayout) findViewById(R.id.ad_container);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Replace wic/LAdmob
                com.google.android.gms.ads.MobileAds.initialize(context, "ca-app-pub-3126660852581586~6866555592");
                final com.google.android.gms.ads.AdView mAdView = (com.google.android.gms.ads.AdView) new com.google.android.gms.ads.AdView(context);
                mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
                mAdView.setAdUnitId("ca-app-pub-3126660852581586/9642614948");


                mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        adContainer.removeAllViews();
                        adContainer.addView(mAdView);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        adContainer.removeAllViews();
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });

                com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                // Add the ad view to your activity layout
                adContainer.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        adView.loadAd();
    }

//    private void openDialogRestartRequest() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.an_error_occurred);
//        builder.setMessage(R.string.please_restart_app);
//        builder.setPositiveButton(R.string.restart_app, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//                restartApp();
//            }
//        });
//        builder.setCancelable(false);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    private void restartApp() {
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuItem_search);
        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.trim();
                if (newText.compareTo("") == 0) {
                    list_suggestions.setVisibility(View.GONE);
                    list_my_words.setVisibility(View.VISIBLE);
                    my_words_heading.setVisibility(View.VISIBLE);
                } else {
                    cursorAdapter.getFilter().filter(newText);
                    
                    list_suggestions.setVisibility(View.VISIBLE);
                    list_my_words.setVisibility(View.GONE);
                    my_words_heading.setVisibility(View.GONE);
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setupMyWordsSortSpinner() {
        final ArrayAdapter<CharSequence> my_words_sort_spinner_adapter = ArrayAdapter.createFromResource(
                this, R.array.my_words_sort_orders, R.layout.small_spinner_item);
        my_words_sort_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_words_sort_spinner.setAdapter(my_words_sort_spinner_adapter);
    }

    private void updateMyWordsDirSpinnerAsync() {
        MyWordsDirSpinnerUpdateTask task = new MyWordsDirSpinnerUpdateTask(
                this,
                myApplication.openRdb,
                my_words_dir_spinner,
                my_words_dir
        );
        task.execute();
    }
}
