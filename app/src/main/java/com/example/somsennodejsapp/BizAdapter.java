package com.example.somsennodejsapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class BizAdapter extends ArrayAdapter<String> {

    Context context;
    String[] rTitle;
    Double[] rDescription;

    BizAdapter(Context c, String[] title, Double[] description){
        super(c, R.layout.listview_row, R.id.textView1, title);
        this.context = c;
        this.rTitle = title;
        this.rDescription=description;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.listview_row, parent, false);
        ImageView icon = row.findViewById(R.id.bizIcon);
        TextView myTitle = row.findViewById(R.id.textView1);
        TextView myDescription = row.findViewById(R.id.textView2);

        myTitle.setText(rTitle[position]);
        myDescription.setText(String.format("%.2f miles", rDescription[position]));

        try {
            //set Image
            String firstWord = rTitle[position].toLowerCase();
            //Check if it's 1 word or more than 1
            if (firstWord.contains(" "))
                firstWord = firstWord.substring(0, rTitle[position].indexOf(" "));

            //Image options
            if (firstWord.equals("harmons"))
                icon.setImageResource(R.drawable.harmons_logo);
            else if (firstWord.equals("smiths"))
                icon.setImageResource(R.drawable.smiths_logo);
            else if (firstWord.equals("sutherlands"))
                icon.setImageResource(R.drawable.sutherlands_logo);
        }catch (Exception e){
            Log.d("ERROR!!!!!!!!!", e.toString());
        }

        return row;
    }
}
