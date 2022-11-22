package com.einundneunzig.animesongstoyoutube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.youtube.YouTubeScopes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Anime anime;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private PopupWindow popupWindow;
    private final int RC_SIGN_IN = 1001;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                // Wenn wir hier ankommen, wurde eine unbekannt Aktion erfasst.
                // Daher erfolgt der Aufruf der Super-Klasse, die sich darum kümmert.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.convertButton:
                account = GoogleSignIn.getLastSignedInAccount(this);
                if (account == null) {
                    signIn();
                }
                showConfirmPopup(account);
                break;

            case R.id.buttonYes:
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                new Thread(() -> {
                    saveThemesToPlaylist();
                    progressBar.setVisibility(View.INVISIBLE);
                    runOnUiThread(()-> Toast.makeText(AnimeDetailsActivity.this, "All Songs added.", Toast.LENGTH_LONG).show());
                }).start();
                popupWindow.dismiss();
                break;

            case R.id.buttonNo:
                popupWindow.dismiss();
                mGoogleSignInClient.signOut();
                account = null;
                Toast.makeText(getApplicationContext(), "Erfolgreich ausgeloggt", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void showConfirmPopup(@NonNull GoogleSignInAccount account) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.confirm_popup, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(findViewById(R.id.convertButton), Gravity.CENTER, 0, 0);
        ((TextView)popupView.findViewById(R.id.doYouWantText)).setText("Möchtest du mit dem YouTube Konto \"" + account.getDisplayName() + "\" fortfahren?");
        popupView.findViewById(R.id.buttonYes).setOnClickListener(this);
        popupView.findViewById(R.id.buttonNo).setOnClickListener(this);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivity(signInIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            account = task.getResult();
        }
    }

    private void saveThemesToPlaylist() {

        ProgressBar progressBar = findViewById(R.id.progressBar);
        try {
            HashMap<String, String> themes = MyAnimeListManager.getThemes(anime.getId());

            CheckBox checkBox1 = findViewById(R.id.checkBoxAllSongs);
            if(checkBox1.isChecked()){
                List<Integer> relatedAnime = MyAnimeListManager.getRelatedAnime(anime.getId());
                for(int id: relatedAnime){
                    themes.putAll(MyAnimeListManager.getThemes(id));
                }
            }
            progressBar.setMax(themes.size());
            progressBar.setIndeterminate(false);

            YoutubeManager.setAccount(account, this);

            String playlistId = YoutubeManager.createPlaylist(anime.getTitle()+" OSTs", "private");

            progressBar.setProgress(1);
            for(Map.Entry<String, String> e: themes.entrySet()) {
                String videoId = YoutubeManager.searchYouTubeForSong(e.getKey(), e.getValue());
                YoutubeManager.addVideoToPlaylist(playlistId, videoId);
                progressBar.setProgress(progressBar.getProgress()+1);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}