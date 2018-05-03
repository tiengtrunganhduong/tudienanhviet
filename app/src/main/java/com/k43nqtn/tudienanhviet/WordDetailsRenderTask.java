package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


class WordDetailsRenderTask extends AsyncTask<Void, Void, Cursor> {

    Context context;
    String word;
    private SQLiteDatabase rdb;
    private String tableName;
    private String dictTitle;
    private LinearLayout detailsContainer;
    private String viewMoreText;
    private String viewLessText;
    private String similarTitleText;
    private Drawable dropDownIcon;
    private Drawable dropUpIcon;
    private Drawable dropTransparentIcon;
    private Values values;


    WordDetailsRenderTask(Context context,
                                 String word,
                                 SQLiteDatabase rdb,
                                 String tableName,
                                 String dictTitle,
                                 LinearLayout detailsContainer
    ) {
        this.context = context;
        this.word = word;
        this.rdb = rdb;
        this.tableName = tableName;
        this.dictTitle = dictTitle;
        this.detailsContainer = detailsContainer;
        this.values = new Values(context);
    }

    @Override
    protected Cursor doInBackground(Void... params) {

        this.viewMoreText = context.getResources().getString(R.string.view_more);
        this.viewLessText = context.getResources().getString(R.string.view_less);
        this.similarTitleText = context.getResources().getString(R.string.similar_title);
        this.dropDownIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down);
        this.dropUpIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_up);
        this.dropTransparentIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_transparent);

        String query = " SELECT "
                    + DictDbContract.COLUMN_WORD + ", "
                    + DictDbContract.COLUMN_DETAILS
                    + " FROM " + tableName
                    + " WHERE " + DictDbContract.COLUMN_WORD + " LIKE @word"
            ;

        return rdb.rawQuery(query, new String[] { word });
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);

        if (cursor != null) {

            final int ALWAYS_VIEW_MAX_LINES = 10;
            final int SEE_MORE_LIST_MIN_LINES = 5;

            while (cursor.moveToNext()) {
                final LinearLayout dictContainer = new LinearLayout(context);
                dictContainer.setOrientation(LinearLayout.VERTICAL);
                detailsContainer.addView(dictContainer);

                dictContainer.addView(createHeadline(dictTitle));

                String meaning = cursor.getString(cursor.getColumnIndex(DictDbContract.COLUMN_DETAILS));
                meaning = meaning.trim();

                if (meaning.compareTo("") != 0) {
                    final ArrayList<View> seeMoreList = new ArrayList<>();
                    if (dictTitle.compareTo(DictDbContract.TITLE_E_1) == 0) {
                        try {
                            JSONArray details = new JSONArray(meaning);
                            ArrayList<View> accurateItems = new ArrayList<>();
                            ArrayList<View> similarItems = new ArrayList<>();
                            for (int i = 0; i < details.length(); i++) {
                                JSONArray detail = details.getJSONArray(i);

                                String syn = detail.getString(0);
                                String def = detail.getString(1);
                                JSONArray examples = detail.getJSONArray(2);
                                JSONArray synonyms = detail.getJSONArray(3);
                                JSONArray antonyms = detail.getJSONArray(4);


                                String[] syn_parts = syn.split("\\.");
                                String syn_title;
//                                Log.wtf("SYN LEN", syn_parts.length + "");
//                                String word_type_html = "";
                                if (syn_parts.length > 2) {
                                    syn_title = syn_parts[0];
                                    for (int j = 1; j < syn_parts.length - 2; j++) {
                                        syn_title += "." + syn_parts[j];
                                    }
//                                    word_type_html = "<i>" + syn_parts[syn_parts.length - 2] + "</i>. ";
                                } else {
                                    syn_title = syn;
                                }

                                final TextView synView = new TextView(context);
                                LinearLayout.LayoutParams synLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                setLineStyle_meaning(synView, synLayoutParams);

                                final boolean syn_title_like_word = syn_title.toLowerCase().compareTo(word.toLowerCase()) == 0;

                                if (syn_title_like_word) {
//                                    Spanned html = Html.fromHtml(def);
                                    synView.setText(def);
                                    accurateItems.add(synView);
                                } else {
                                    Spanned html = Html.fromHtml("<b><u>" + syn_title + "</u></b> " + def);
                                    synView.setText(html);
                                    similarItems.add(synView);
                                    synView.setTextIsSelectable(false);

                                    final String linked_syn_title = syn_title;

                                    synView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            synView.setBackgroundColor(Color.parseColor("#EEEEEE"));

                                            Intent intent = new Intent(context, WordActivity.class);
                                            intent.putExtra("dir", "ev");
                                            intent.putExtra("word", linked_syn_title);
                                            intent.putExtra("last_lookup_time", "NOW");

                                            context.startActivity(intent);
                                        }
                                    });
                                }



                                // Examples
                                for (int j = 0; j < examples.length(); j++) {
                                    TextView exampleView = new TextView(context);
                                    LinearLayout.LayoutParams exampleLayoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    setLineStyle_example(exampleView, exampleLayoutParams);
                                    exampleView.setText(examples.getString(j));
                                    if (syn_title_like_word) {
                                        accurateItems.add(exampleView);
                                    } else {
                                        similarItems.add(exampleView);
                                    }
                                }


                                // Synonyms
                                if (synonyms.length() > 0) {
                                    ArrayList<String> synonymList = new ArrayList<>();
                                    for (int j = 1; j < synonyms.length(); j++) {
                                        if (synonyms.getString(j).toLowerCase().compareTo(syn.toLowerCase()) != 0
                                            && synonyms.getString(j).toLowerCase().compareTo(word.toLowerCase()) != 0
                                        ) {
                                            synonymList.add(synonyms.getString(j));
                                        }
                                    }
                                    if (synonymList.size() > 0) {
                                        String synonymsText;
                                        if (synonymList.size() > 1) {
                                            synonymsText = "~ " + synonymList.get(0);
                                            for (int j = 1; j < synonymList.size(); j++) {
                                                synonymsText += ", " + synonymList.get(j);
                                            }
                                        } else {
                                            synonymsText = "~ " + synonymList.get(0);
                                        }
                                        TextView synonymsView = new TextView(context);
                                        LinearLayout.LayoutParams antonymLayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        setLineStyle_synonyms(synonymsView, antonymLayoutParams);
                                        synonymsView.setText(synonymsText);
                                        if (syn_title_like_word) {
                                            accurateItems.add(synonymsView);
                                        } else {
                                            similarItems.add(synonymsView);
                                        }
                                    }
                                }
                                
                                // Antonyms
                                if (antonyms.length() > 0) {
                                    String antonymsText;
                                    if (antonyms.length() > 1) {
                                        antonymsText = ">< " + antonyms.getString(0);
                                        for (int j = 1; j < antonyms.length(); j++) {
                                            antonymsText += ", " + antonyms.getString(j);
                                        }
                                    } else {
                                        antonymsText = ">< " + antonyms.getString(0);
                                    }
                                    TextView antonymsView = new TextView(context);
                                    LinearLayout.LayoutParams antonymLayoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    setLineStyle_antonyms(antonymsView, antonymLayoutParams);
                                    antonymsView.setText(antonymsText);
                                    if (syn_title_like_word) {
                                        accurateItems.add(antonymsView);
                                    } else {
                                        similarItems.add(antonymsView);
                                    }
                                }
                                
                                

                            }
                            int k = 0;
                            for (View view: accurateItems) {
                                if (k > ALWAYS_VIEW_MAX_LINES) {
                                    seeMoreList.add(view);
                                } else {
                                    dictContainer.addView(view);
                                }
                                k++;
                            }
                            if (similarItems.size() > 0) {
                                TextView similarTitle = new TextView(context);
                                LinearLayout.LayoutParams similarTitleLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                setLineStyle_similarTitle(similarTitle, similarTitleLayoutParams);
                                similarTitle.setText(similarTitleText);

                                if (k > ALWAYS_VIEW_MAX_LINES) {
                                    seeMoreList.add(similarTitle);
                                } else {
                                    dictContainer.addView(similarTitle);
                                }
                                k++;

                                for (View view: similarItems) {
                                    if (k > ALWAYS_VIEW_MAX_LINES) {
                                        seeMoreList.add(view);
                                    } else {
                                        dictContainer.addView(view);
                                    }
                                    k++;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String[] meaningLines = meaning.split("\n");
                        for (int i = 0; i < meaningLines.length; i++) {
                            String line = meaningLines[i];
                            if (line.trim().compareTo("") == 0) {
                                continue;
                            }

                            TextView meaningLineView = new TextView(context);
                            LinearLayout.LayoutParams meaningLineLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            if (dictTitle.compareTo(DictDbContract.TITLE_EV_1) == 0
//                                || dictTitle.compareTo(DictDbContract.TITLE_EV_2) == 0
                                || dictTitle.compareTo(DictDbContract.TITLE_VE_1) == 0
                                || dictTitle.compareTo(DictDbContract.TITLE_VE_2) == 0
                            ) {
                                switch (line.charAt(0)) {
                                    case '/':
                                        setLineStyle_spelling(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '*':
                                        line = "* " + line.substring(1).trim();
                                        setLineStyle_wordType(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '-':
                                        line = line.substring(1).trim();
                                        setLineStyle_meaning(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '=':
                                        line = line.substring(1);
                                        int plusPos = line.indexOf('+');
                                        if (plusPos > -1) {
                                            String leftStr = line.substring(0, plusPos).trim();
                                            String rightStr = line.substring(plusPos + 1).trim();
                                            line = leftStr + ": " + rightStr;
                                        } else {
                                            line = line.trim();
                                        }
                                        setLineStyle_example(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '!':
                                        line = line.substring(1).trim();
                                        setLineStyle_idiom(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '&':
                                        line = "Chuyên ngành " + line.substring(1).trim() + ":";
                                        setLineStyle_specializedTitle(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    case '%':
                                        line = "Lĩnh vực " + line.substring(1).trim() + ":";
                                        setLineStyle_fieldTitle(meaningLineView, meaningLineLayoutParams);
                                        break;
                                    default:
                                        setLineStyle_meaning(meaningLineView, meaningLineLayoutParams);
                                }
                            } else {
                                setLineStyle_meaning(meaningLineView, meaningLineLayoutParams);
                            }

                            meaningLineView.setText(line);

                            if (i > ALWAYS_VIEW_MAX_LINES) {
                                seeMoreList.add(meaningLineView);
                            } else {
                                dictContainer.addView(meaningLineView);
                            }

                        }
                    }


                    if (seeMoreList.size() >= SEE_MORE_LIST_MIN_LINES) {
                        final TextView seeMoreClickableInner = new TextView(context);
                        seeMoreClickableInner.setPaintFlags(seeMoreClickableInner.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        seeMoreClickableInner.setTextColor(values.COLOR_BLUE);
                        seeMoreClickableInner.setPadding(values.DP_5, values.DP_5, values.DP_5, values.DP_5);
                        seeMoreClickableInner.setGravity(Gravity.CENTER);
                        seeMoreClickableInner.setText(viewMoreText);
                        seeMoreClickableInner.setCompoundDrawablesWithIntrinsicBounds(
                                dropDownIcon, null, dropTransparentIcon, null
                        );

                        final LinearLayout seeMoreClickable = new LinearLayout(context);
                        LinearLayout.LayoutParams seeMoreLayoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        seeMoreLayoutParams.setMargins(0, values.DP_10, 0, 0);
                        seeMoreClickable.setLayoutParams(seeMoreLayoutParams);
                        seeMoreClickable.setGravity(Gravity.CENTER);
                        seeMoreClickable.addView(seeMoreClickableInner);

                        seeMoreClickable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (seeMoreClickableInner.getText().toString().compareTo(viewMoreText) == 0) {
                                    for (View lineView : seeMoreList) {
                                        // append before view more button
                                        dictContainer.addView(lineView, dictContainer.getChildCount() - 1);
                                    }
                                    seeMoreClickableInner.setText(viewLessText);
                                    seeMoreClickableInner.setCompoundDrawablesWithIntrinsicBounds(
                                            dropUpIcon, null, dropTransparentIcon, null
                                    );
                                } else {
                                    for (View lineView : seeMoreList) {
                                        dictContainer.removeView(lineView);
                                    }
                                    seeMoreClickableInner.setText(viewMoreText);
                                    seeMoreClickableInner.setCompoundDrawablesWithIntrinsicBounds(
                                            dropDownIcon, null, dropTransparentIcon, null
                                    );
                                }
                            }
                        });
                        dictContainer.addView(seeMoreClickable);
                    } else {
                        for (View lineView : seeMoreList) {
                            dictContainer.addView(lineView);
                        }
                    }
                }
            }
            cursor.close();
        }
    }
    
    private TextView createHeadline(String text) {
        TextView divider = new TextView(context);
        LinearLayout.LayoutParams dividerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dividerLayoutParams.setMargins(0, values.DP_15, 0, 0);
        divider.setPadding(values.DP_5, values.DP_2, values.DP_5, values.DP_2);
        divider.setLayoutParams(dividerLayoutParams);
        divider.setBackgroundColor(values.COLOR_GREY_BG);
        divider.setTextColor(Color.WHITE);
        divider.setTextSize(12);
        divider.setText(text.toUpperCase());
        return divider;
    }

    private void setLineStyle_spelling(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_10, 0, 0);
        textView.setTextColor(values.COLOR_RED);
        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_wordType(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_10, 0, 0);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(values.COLOR_GREY_MD);
        textView.setLayoutParams(layoutParams);
    }
    private void setLineStyle_meaning(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(values.DP_10, values.DP_10, 0, 0);
        textView.setTextSize(19);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_example(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(values.DP_20, values.DP_5, 0, 0);
        textView.setTextSize(16);
        textView.setTextColor(values.COLOR_GREY);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_synonyms(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(values.DP_20, values.DP_5, 0, 0);
        textView.setTextSize(16);
        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
        textView.setTextColor(values.COLOR_GREY_DK);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_antonyms(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(values.DP_20, values.DP_5, 0, 0);
        textView.setTextSize(16);
        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
        textView.setTextColor(values.COLOR_GREY_DK);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_idiom(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_10, 0, 0);
        textView.setTextSize(16);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
        textView.setTextColor(values.COLOR_BLUE);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_similarTitle(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_10, 0, 0);
        textView.setTextSize(16);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(values.COLOR_YELLOW);
        textView.setLayoutParams(layoutParams);
        textView.setTextIsSelectable(true);
    }
    private void setLineStyle_specializedTitle(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_15, 0, 0);
        textView.setTextSize(15.5f);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(values.COLOR_YELLOW);
        textView.setLayoutParams(layoutParams);
    }
    private void setLineStyle_fieldTitle(TextView textView, LinearLayout.LayoutParams layoutParams) {
        layoutParams.setMargins(0, values.DP_15, 0, 0);
        textView.setTextSize(15);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(values.COLOR_GREY);
        textView.setLayoutParams(layoutParams);
    }

}
