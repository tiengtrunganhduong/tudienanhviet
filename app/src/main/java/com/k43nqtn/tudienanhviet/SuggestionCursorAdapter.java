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
      String word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_TITLE));

      if (word == null || word.compareTo("") == 0) {
          word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_LOWERCASE));
      }

      if (2 == lang) {
          final String word_html = word +
                  " &nbsp;<font color='#D1D1D1'><small><small><small><i><b>(&nbsp;English&nbsp;only&nbsp;)</b></i></small></small></small></font>";
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              txt_word.setText(Html.fromHtml(word_html, Html.FROM_HTML_MODE_COMPACT));
          } else {
              txt_word.setText(Html.fromHtml(word_html));
          }
      } else {
          txt_word.setText(word);
      }

//      if (0 == lang || 2 == lang) {
//          img_arrow.setImageResource(R.drawable.ic_chevron_en_vi);
//      } else {
//          img_arrow.setImageResource(R.drawable.ic_chevron_vi_en);
//      }
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