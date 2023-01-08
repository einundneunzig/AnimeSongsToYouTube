package com.einundneunzig.animesongstoyoutube.youtube;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class YoutubeManager {

    private static YouTube mService;
    private static GoogleSignInAccount signInAccount;

    private static List<Playlist> usersPlaylists;

    public static void setAccount(@NonNull GoogleSignInAccount account, Context context){

        signInAccount = account;

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Collections.singletonList(YouTubeScopes.YOUTUBE_FORCE_SSL));
        credential.setSelectedAccount(account.getAccount());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("AnimeSongsToYouTube")
                .build();

    }
    public static GoogleSignInAccount getAccount(){
        return signInAccount;
    }

    public static void removeAccount(){
        signInAccount = null;
        mService = null;
    }
    public static List<Playlist> findPlaylistsByName(String name){

        if(usersPlaylists==null) loadUsersPlaylists();

        List<Playlist> playlistsWithName = new ArrayList<>();

        for (Playlist playlist : usersPlaylists) {
            if (playlist.getSnippet().getTitle().toLowerCase().contains(name.toLowerCase())) {
                playlistsWithName.add(playlist);
            }
        }

        return playlistsWithName;
    }

    public static void loadUsersPlaylists(){

        if(!checkMService()) return;

        final List<Playlist> playlists = new ArrayList<>();

        final String[] pageToken = {" "};

        while (pageToken[0] != null) {

            final PlaylistListResponse[] response = {null};

            Thread t = new Thread(()->{
                try {
                    response[0] = mService.playlists().list("snippet")
                            .setMine(true)
                            .setPageToken(pageToken[0])
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();

            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (response[0].size() == 0) break;

            playlists.addAll(response[0].getItems());

            pageToken[0] = response[0].getNextPageToken();
        }
        usersPlaylists = playlists;
    }

    public static void addVideoToPlaylist(@NonNull String playlistId, @NonNull String videoId){

        if(!checkMService()) return;

        PlaylistItem item = new PlaylistItem();
        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setPlaylistId(playlistId);
        snippet.setResourceId(new ResourceId().setKind("youtube#video").setVideoId(videoId));
        item.setSnippet(snippet);

        Thread t = new Thread(() -> {
            try {
                mService.playlistItems().insert("snippet", item).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static String createPlaylist(String title, String privacyStatus) throws IOException {

        if(!checkMService()) return "";

        Playlist playlist = new Playlist();
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus(privacyStatus);
        playlist.setStatus(status);
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(title);
        playlistSnippet.setDefaultLanguage("DE");
        playlist.setSnippet(playlistSnippet);

        Playlist[] response = {null};
        Thread t = new Thread(() -> {
            try {
                response[0] = mService.playlists().insert("snippet,status", playlist).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response[0].getId();
    }

    public static SearchResult searchYouTubeForSong(String title, String singer){

        if(!checkMService()) return null;

        final SearchResult[] searchResult = new SearchResult[1];


        Thread t = new Thread(() -> {
            SearchListResponse searchListResponse = null;
            try {
                searchListResponse = mService.search().list("snippet").setQ(title + " " + singer).setType("video").setVideoCategoryId("10").execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(searchListResponse != null && searchListResponse.getItems().size() != 0){
                searchResult[0] = searchListResponse.getItems().get(0);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(searchResult[0]==null) {
            return searchYouTubeForSong(title, "");
        }
        return searchResult[0];
    }

    private static boolean checkMService() {
        if(mService == null){
            Log.w("997", "YouTube is not accessible. Were the Account set?");
            return false;
        }
        return true;
    }


}
