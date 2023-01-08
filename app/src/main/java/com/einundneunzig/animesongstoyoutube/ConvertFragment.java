package com.einundneunzig.animesongstoyoutube;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ConvertFragment extends Fragment {

    private TableLayout tableLayout;

    public ConvertFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_convert_layout, container, false);

        tableLayout = rootView.findViewById(R.id.convert_table_layout);

        ArrayList<String> animeSongTitles = getArguments().getStringArrayList("animeSongTitles");
        ArrayList<String> animeSongSingers = getArguments().getStringArrayList("animeSongSingers");
        ArrayList<String> youtubeVideoTitles = getArguments().getStringArrayList("youtubeVideoTitles");
        ArrayList<String> youtubeVideoChannels = getArguments().getStringArrayList("youtubeVideoChannels");

        for(int i = 0; i<animeSongTitles.size(); i++){
            addRow(animeSongTitles.get(i), animeSongSingers.get(i), youtubeVideoTitles.get(i), youtubeVideoChannels.get(i));
            addRow("t" , "t", "t", "t");
            addRow("t" , "t", "t", "t");
        }

        return rootView;
    }

    private void addRow(String animeSongTitle, String animeSongSinger, String youtubeTitle, String youtubeChannel){
        TableRow tableRow = new TableRow(getContext());

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f);

        TextView animeListTextView = new TextView(getContext());
        animeListTextView.setText(animeSongTitle);
        animeListTextView.setLayoutParams(layoutParams);

        TextView youtubeTextView = new TextView(getContext());
        youtubeTextView.setText(youtubeTitle);
        youtubeTextView.setLayoutParams(layoutParams);

        Button button = new Button(getContext());
        button.setText("o");
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.2f));

        tableRow.addView(animeListTextView);
        tableRow.addView(youtubeTextView);
        tableRow.addView(button);

        tableLayout.addView(tableRow);
    }
}