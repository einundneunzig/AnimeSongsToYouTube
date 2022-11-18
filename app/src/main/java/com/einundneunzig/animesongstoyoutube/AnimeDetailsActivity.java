package com.einundneunzig.animesongstoyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Anime anime;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private PopupWindow popupWindow;
    private YouTube mService;
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveThemesToPlaylist();
                    }
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

    private void showConfirmPopup(GoogleSignInAccount account) {
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

        try {
            HashMap<String, String> openingThemes = getThemes(
                    new URL("https://api.myanimelist.net/v2/anime/" + anime.getId() + "?fields=opening_themes{text}"));

            HashMap<String, String> endingThemes = getThemes(
                    new URL("https://api.myanimelist.net/v2/anime/" + anime.getId() + "?fields=ending_themes{text}"));

            CheckBox checkBox1 = findViewById(R.id.checkBoxAllSongs);
            if(checkBox1.isChecked()){
                getRelatedAnime(new URL("https://api.myanimelist.net/v2/anime/" + anime.getId() + "?fields=related_anime"));
            }

            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(YouTubeScopes.YOUTUBE_FORCE_SSL));
            credential.setSelectedAccount(account.getAccount());

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();

            Playlist playlist = new Playlist();
            PlaylistStatus status = new PlaylistStatus();
            status.setPrivacyStatus("private");
            playlist.setStatus(status);
            PlaylistSnippet playlistSnippet = new PlaylistSnippet();
            playlistSnippet.setTitle(anime.getTitle() + " OSTs");
            playlistSnippet.setDefaultLanguage("DE");
            playlist.setSnippet(playlistSnippet);

            Playlist[] response = {null};
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        response[0] = mService.playlists().insert("snippet,status", playlist).execute();
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

            for(Map.Entry<String, String> e: openingThemes.entrySet()) {
                String videoId = getYouTubeLink(e.getKey() + " " + e.getValue());
                addToPlaylist(response[0].getId(), videoId);
            }

            for(Map.Entry<String, String> e: endingThemes.entrySet()) {
                String videoId = getYouTubeLink(e.getKey() + " " + e.getValue());
                addToPlaylist(response[0].getId(), videoId);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private String getYouTubeLink(String song){
        final SearchResult[] searchResult = new SearchResult[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SearchListResponse searchListResponse = null;
                try {
                    searchListResponse = mService.search().list("snippet").setQ(song).setType("video").setVideoCategoryId("10").execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(song);
                searchResult[0] = searchListResponse.getItems().get(0);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return searchResult[0].getId().getVideoId();
    }

    private void addToPlaylist(String playlistId, String videoId) {

        PlaylistItem item = new PlaylistItem();
        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setPlaylistId(playlistId);
        snippet.setResourceId(new ResourceId().setKind("youtube#video").setVideoId(videoId));
        item.setSnippet(snippet);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mService.playlistItems().insert("snippet", item).execute();
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
    }

    private List<Integer> getRelatedAnime(URL url)throws IOException{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = response.readLine();
        System.out.println(line);
        return null;
    }
    private HashMap<String, String> getThemes(URL url) throws IOException {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = response.readLine();

        int anfang = line.indexOf("\"text\":");
        int ende = 0;

        ArrayList<String> themes = new ArrayList<>();
        HashMap<String, String> seperate = new HashMap<>();

        while (anfang != -1) {
            anfang += 8;                //remove "text":"
            anfang = line.indexOf("\"", anfang) + 1;

            ende = line.indexOf("}", anfang) - 1;
            themes.add(line.substring(anfang, ende));
            anfang = line.indexOf("\"text\":", ende);
        }

        for (String s : themes) {
            int i = s.indexOf("(");
            if(!(i<s.indexOf("\""))){
                i = s.indexOf("\"");
            }
            int end = s.indexOf("(", i + 1);
            if (end != -1) end--;
            else end = s.length();
            seperate.put(s.substring(0, i - 1).replaceAll("\\\\u....", ""), s.substring(s.indexOf("\"") + 5, end).replaceAll("\\\\u....", ""));
        }

        return seperate;
    }
}