package com.einundneunzig.animesongstoyoutube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.youtube.YouTubeScopes;

import java.io.Serializable;

public class AnimeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Anime anime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        anime = (Anime) intent.getSerializableExtra("anime");

        setContentView(R.layout.activity_anime_details);
        ImageView animeImage = findViewById(R.id.animeImageView);
        TextView animeTitle = findViewById(R.id.animeTitleView);
        animeImage.setImageBitmap(anime.getImage());
        animeTitle.setText(anime.getTitle());
        findViewById(R.id.convertButton).setOnClickListener(this);
        
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.convertButton){
            saveThemesToPlaylist();
        }
    }

    private void saveThemesToPlaylist() {

    }
}