package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class SuggestionCursorAdapter extends CursorAdapter {
  SuggestionCursorAdapter(Context context, Cursor cursor) {
      super(context, cursor, 0);
  }

  // The newView method is used to inflate a new view and return it, 
  // you don't bind any data to the view at this point. 
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
  }

  // The bindView method is used to bind all data to a given view
  // such as setting the text on a TextView.
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
      // Find fields to populate in inflated template
      TextView txt_word = view.findViewById(R.id.txt_word);
      ImageView img_arrow = view.findViewById(R.id.img_arrow);

      // Extract properties from cursor
      int lang = cursor.getInt(cursor.getColumnIndex(WordsDbContract.COLUMN_LANG));

//      String word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_TRANSLATION));
//      if (word == null || word.compareTo("") == 0) {
//          word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_LOWERCASE));
//      }

      String word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_LOWERCASE));
      String translation = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_TRANSLATION));

      /*
      if (2 == lang) {
          final String word_html = word +
                  " <font color='#D1D1D1'><small><small><i>&nbsp;&nbsp;&mdash;&nbsp;English&nbsp;Only</i></small></small></font>";
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              txt_word.setText(Html.fromHtml(word_html, Html.FROM_HTML_MODE_COMPACT));
          } else {
              txt_word.setText(Html.fromHtml(word_html));
          }
      } else {
          txt_word.setText(word);
      }
      */

      if (!word.contains(" ") && translation != null && translation.compareTo("") != 0) {
          final String word_html = word +
                  " <br><font color='#CCCCCC'><small><small><i>" + translation + "</i></small></small></font>";
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              txt_word.setText(Html.fromHtml(word_html, Html.FROM_HTML_MODE_COMPACT));
          } else {
              txt_word.setText(Html.fromHtml(word_html));
          }
      } else if (2 == lang) {
          final String word_html = word +
                  " <br><font color='#CCCCCC'><small><small><i>(English Only)</i></small></small></font>";
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              txt_word.setText(Html.fromHtml(word_html, Html.FROM_HTML_MODE_COMPACT));
          } else {
              txt_word.setText(Html.fromHtml(word_html));
          }
      } else {
          txt_word.setText(word);
      }


      switch (lang) {
          case 0:
              img_arrow.setImageResource(R.drawable.ic_chevron_en_vi);
              break;
          case 1:
              img_arrow.setImageResource(R.drawable.ic_chevron_vi_en);
              break;
          case 2:
              img_arrow.setImageResource(R.drawable.ic_chevron_en_en);
              break;
          default:
              img_arrow.setImageResource(R.drawable.ic_chevron_en_vi);
      }
  }
}