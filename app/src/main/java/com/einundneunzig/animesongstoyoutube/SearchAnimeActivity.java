package com.einundneunzig.animesongstoyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.einundneunzig.animesongstoyoutube.myanimelist.AnimeList;
import com.einundneunzig.animesongstoyoutube.myanimelist.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;

public class SearchAnimeActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    private LinearLayout animeListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_anime);
        EditText textField = findViewById(R.id.searchAnimeTextField);
        textField.setOnEditorActionListener(this);
        animeListLayout = findViewById(R.id.animeList);
    }

    private void searchAnime(String search) {


        final AnimeList[] animeList = {null};

            Thread t = new Thread(() -> {

                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();

                try {
                URL url = new URL("https://api.myanimelist.net/v2/anime?q=" + search + "&limit=10&nsfw=true");
                animeList[0] = mapper.readValue(MyAnimeListManager.getAPIResponse(url), AnimeList.class);

                } catch (IOException e) { e.printStackTrace(); }
            });

            t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateAnimeList(animeList[0]);
    }

    private void updateAnimeList(AnimeList animeList) {
        animeListLayout.removeAllViews();

        //Creating Horizontal LinearLayout for each node (Anime from search Result), giving it a Tag with it's Id and adding it to animeListLayout
        for(Node node: animeList.getData()){
            LinearLayout nodeLayout = new LinearLayout(getApplicationContext());
            nodeLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
            nodeLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView image = new ImageView(this);
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 3));
            image.setImageBitmap(node.getMain_picture().getMediumBitmap());

            TextView text = new TextView(this);
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 1));
            text.setText(node.getTitle());

            nodeLayout.addView(image);
            nodeLayout.addView(text);

            nodeLayout.setTag(node.getId());
            nodeLayout.setOnClickListener(this);
            animeListLayout.addView(nodeLayout);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            if(v.getText().length()>2){
                searchAnime(v.getText().toString());
            }else{
                Toast.makeText(this, "Bitte gebe mindestens 3 Buchstaben ein.", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof LinearLayout){
            LinearLayout animeLayout = (LinearLayout) view;
            if(((LinearLayout) view).getOrientation() == LinearLayout.HORIZONTAL){
                int id = (int) animeLayout.getTag();
                Intent intent = new Intent(this, AnimeDetailsActivity.class);
                intent.putExtra("animeId", id);
                startActivity(intent);
            }
        }
    }
}