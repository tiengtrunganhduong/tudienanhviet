package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class SuggestionArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
    Context context;
    ArrayList<HashMap<String, String>> suggestionList = new ArrayList<>();

    public SuggestionArrayAdapter(@NonNull Context context, ArrayList<HashMap<String, String>> suggestionList) {
        super(context, R.layout.activity_main);
        this.context = context;
        this.suggestionList = suggestionList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        }

        HashMap<String, String> currentSuggestion = suggestionList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.img_arrow);
        if (currentSuggestion.get("dir").compareTo("ev") == 0) {
            image.setImageResource(R.drawable.ic_chevron_en_vi);
        } else {
            image.setImageResource(R.drawable.ic_chevron_vi_en);
        }

        TextView name = (TextView) listItem.findViewById(R.id.txt_word);
        name.setText(currentSuggestion.get("word"));

        return listItem;
    }

    @Override
    public int getCount() {
        return suggestionList.size();
    }
}
