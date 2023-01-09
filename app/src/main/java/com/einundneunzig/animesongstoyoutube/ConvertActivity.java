package com.einundneunzig.animesongstoyoutube;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConvertActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    public ConvertActivity(){

        runOnUiThread(()-> Toast.makeText(this, "All Songs added.", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_layout);

        tableLayout = findViewById(R.id.convert_table_layout);

        Bundle bundle = getIntent().getExtras();

        //Add TableRows (Song comparison)
        ArrayList<String> animeSongTitles = bundle.getStringArrayList("animeSongTitles");
        ArrayList<String> animeSongSingers = bundle.getStringArrayList("animeSongSingers");
        ArrayList<String> youtubeVideoTitles = bundle.getStringArrayList("youtubeVideoTitles");
        ArrayList<String> youtubeVideoChannels = bundle.getStringArrayList("youtubeVideoChannels");

        for (int i = 0; i < animeSongTitles.size(); i++) {
            addRow(animeSongTitles.get(i), animeSongSingers.get(i), youtubeVideoTitles.get(i), youtubeVideoChannels.get(i));
        }

    }

    private void addRow(String animeSongTitle, String animeSongSinger, String youtubeTitle, String youtubeChannel){
        TableRow tableRow = new TableRow(this);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f);

        TextView animeListTextView = new TextView(this);
        animeListTextView.setText(animeSongTitle);
        animeListTextView.setLayoutParams(layoutParams);

        TextView youtubeTextView = new TextView(this);
        youtubeTextView.setText(youtubeTitle);
        youtubeTextView.setLayoutParams(layoutParams);

        Button button = new Button(this);
        button.setText("o");
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.2f));

        tableRow.addView(animeListTextView);
        tableRow.addView(youtubeTextView);
        tableRow.addView(button);

        tableLayout.addView(tableRow);
    }
}