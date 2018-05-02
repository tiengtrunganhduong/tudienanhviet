package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class MyWordCursorAdapter extends CursorAdapter {
  MyWordCursorAdapter(Context context, Cursor cursor) {
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
      txt_word.setText(cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_WORD)));

      String dir = cursor.getString(cursor.getColumnIndex(OpenDbContract.MyWord.COL_DIR));

      if (dir.compareTo("ev") == 0) {
          img_arrow.setImageResource(R.drawable.ic_chevron_en_vi);
      } else {
          img_arrow.setImageResource(R.drawable.ic_chevron_vi_en);
      }
  }
}