package com.einundneunzig.animesongstoyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SearchAnimeActivity extends AppCompatActivity implements View.OnKeyListener{

    private ScrollView animeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_anime);
        EditText textField = findViewById(R.id.searchAnimeTextField);
        textField.setOnKeyListener(this);
        animeListView = findViewById(R.id.animeList);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(view.getId()==R.id.searchAnimeTextField && keyEvent.getAction() == KeyEvent.ACTION_UP){
            EditText textField = findViewById(R.id.searchAnimeTextField);
            String text = textField.getText().toString();
            searchAnime(text);
        }
        return false;
    }

    private void searchAnime(String text) {

    }
}