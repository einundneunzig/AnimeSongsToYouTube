package com.einundneunzig.animesongstoyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchAnimeActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    private LinearLayout animeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_anime);
        EditText textField = findViewById(R.id.searchAnimeTextField);
        textField.setOnEditorActionListener(this);
        animeList = findViewById(R.id.animeList);
    }

    private void searchAnime(String search) {

        List<Anime> loadedAnime = new ArrayList<>();

        final String[] response = {null};

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                    URL url = new URL("https://api.myanimelist.net/v2/anime?q=" + search + "&limit=10&nsfw=true");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
                    con.getInputStream();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    response[0] = responseReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            });

            t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(response[0] ==null) ;//Fehler
        else{
            int lastIndex = response[0].indexOf("\"id\"");
            while(lastIndex!=-1){
                int id;
                String title;
                URL photoUrl = null;


                int idPosBeg = lastIndex+5;
                int idPosEd = response[0].indexOf(",",idPosBeg);
                id = Integer.parseInt(response[0].substring(idPosBeg, idPosEd));

                int titlePosBeg = idPosEd+10;
                int titlePosEd = response[0].indexOf(",",titlePosBeg);
                title = response[0].substring(titlePosBeg, titlePosEd-1);

                int photoPosBeg = titlePosEd+27;
                int photoPosEd = response[0].indexOf(",",photoPosBeg);
                try {
                    photoUrl = new URL(response[0].substring(photoPosBeg, photoPosEd-1));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                lastIndex = response[0].indexOf("\"id\"",photoPosEd);
                loadedAnime.add(new Anime(id, title, photoUrl));
            }
        }

        updateAnimeList(loadedAnime);
    }

    private void updateAnimeList(List<Anime> loadedAnime) {
        animeList.removeAllViews();
        for(Anime anime: loadedAnime){
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);


            ImageView image = new ImageView(this);
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 3));
            image.setImageBitmap(anime.getImage());

            TextView text = new TextView(this);
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 1));
            text.setText(anime.getTitle());

            layout.addView(image);
            layout.addView(text);

            layout.setTag(anime);
            layout.setOnClickListener(this);
            animeList.addView(layout);
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
                Anime anime = (Anime)animeLayout.getTag();
                Intent intent = new Intent(this, AnimeDetailsActivity.class);
                intent.putExtra("anime", anime);
                startActivity(intent);
            }
        }
    }
}