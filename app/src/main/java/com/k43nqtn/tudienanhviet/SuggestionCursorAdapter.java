package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.database.Cursor;
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
      String word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_TITLE));
      if (word == null || word.compareTo("") == 0) {
          word = cursor.getString(cursor.getColumnIndex(WordsDbContract.COLUMN_LOWERCASE));
      }
      txt_word.setText(word);

      int lang = cursor.getInt(cursor.getColumnIndex(WordsDbContract.COLUMN_LANG));
      if (0 == lang) {
          img_arrow.setImageResource(R.drawable.ic_chevron_en_vi);
      } else {
          img_arrow.setImageResource(R.drawable.ic_chevron_vi_en);
      }
  }
}