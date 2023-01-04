package com.einundneunzig.animesongstoyoutube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.einundneunzig.animesongstoyoutube.myanimelist.Node;
import com.einundneunzig.animesongstoyoutube.myanimelist.RelationType;
import com.einundneunzig.animesongstoyoutube.myanimelist.Theme;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.youtube.YouTubeScopes;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnimeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Node node;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private PopupWindow popupWindow;
    private final int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        account = GoogleSignIn.getLastSignedInAccount(this);
        YoutubeManager.setAccount(account, this);

        node = MyAnimeListManager.getAnimeDetails(intent.getIntExtra("animeId", -1));

        setContentView(R.layout.activity_anime_details);
        ImageView animeImage = findViewById(R.id.animeImageView);
        TextView animeTitle = findViewById(R.id.animeTitleView);

        animeImage.setImageBitmap(node.getMain_picture().getLargeBitmap());
        animeTitle.setText(node.getTitle());

        findViewById(R.id.convertButton).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .requestEmail()
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
                if (account == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.not_logged_in), Toast.LENGTH_LONG).show();
                }else{
                    showConfirmPopup(account);
                }
                break;

            case R.id.buttonYes:
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                new Thread(() -> {
                    saveThemesToPlaylist();
                    progressBar.setVisibility(View.INVISIBLE);
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
        ArrayList<String> playlistIds = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getBoolean("add_to_playlist", false)){
            String addToPlaylistId = sharedPreferences.getString("add_to_playlist_id", "");

            if(addToPlaylistId == ""){
                runOnUiThread(()-> Toast.makeText(AnimeDetailsActivity.this, getString(R.string.playlist_id_empty), Toast.LENGTH_LONG).show());
                return;
            }
            playlistIds.add(addToPlaylistId);
        }

        boolean sequels = sharedPreferences.getBoolean("sequels", false);
        boolean prequels = sharedPreferences.getBoolean("prequels", false);
        boolean others = sharedPreferences.getBoolean("others", false);
        boolean side_stories = sharedPreferences.getBoolean("side_stories", false);
        boolean spin_offs = sharedPreferences.getBoolean("spin_offs", false);
        boolean all = sharedPreferences.getBoolean("all", false);

        Set<Theme> themes = new HashSet<>();
        themes.addAll(Arrays.asList(node.getOpeningThemes()));
        themes.addAll(Arrays.asList(node.getEndingThemes()));

        if(all){
            themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.sequel));
            themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.prequel));
            themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.side_story));
            themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.spin_off));
            themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.other));
        }else{

            if(sequels){
                themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.sequel));
            }
            if(prequels){
                themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.prequel));
            }
            if(side_stories){
                themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.side_story));
            }
            if(spin_offs){
                themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.spin_off));
            }
            if(others){
                themes.addAll(MyAnimeListManager.getThemes(node.getRelatedAnime(), RelationType.other));
            }

        }

        if(sharedPreferences.getBoolean("create_new_playlist", false)){

            String playlistName = sharedPreferences.getString("create_new_playlist_name", "[name] OSTs").replace("[name]", node.getTitle());

            String privacyStatus = sharedPreferences.getString("privacy_status_drop_down", "public");

            try {
                playlistIds.add(YoutubeManager.createPlaylist(playlistName, privacyStatus));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        progressBar.setMax(themes.size()*playlistIds.size());
        progressBar.setIndeterminate(false);
        progressBar.setProgress(1);

        for(Theme theme: themes) {
            String videoId = YoutubeManager.searchYouTubeForSong(theme.getTitle(), theme.getSinger());
            for(String playlistId: playlistIds){
                YoutubeManager.addVideoToPlaylist(playlistId, videoId);
                progressBar.setProgress(progressBar.getProgress()+1);
            }
        }

        runOnUiThread(()-> Toast.makeText(AnimeDetailsActivity.this, "All Songs added.", Toast.LENGTH_LONG).show());
    }
}