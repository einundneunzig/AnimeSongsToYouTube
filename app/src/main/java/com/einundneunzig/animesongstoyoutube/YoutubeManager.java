package com.einundneunzig.animesongstoyoutube;

import android.content.Context;

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
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class YoutubeManager {

    private static YouTube mService;

    public static void setAccount(GoogleSignInAccount account, Context context){

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(YouTubeScopes.YOUTUBE_FORCE_SSL));
        credential.setSelectedAccount(account.getAccount());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
    }

    public static Playlist findPlaylistById(String id){
        return null;
    }
    public static List<Playlist> getAllPlaylistsFromUser(){
        return null;
    }
    public static void addVideoToPlaylist(String playlistId, String videoId){

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
    public static String createPlaylist(String title, String privacyStatus) throws IOException {

        if(mService == null){
            throw new IOException("YouTube is not accessible. Were the Account set?");
        }
        Playlist playlist = new Playlist();
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus(privacyStatus);
        playlist.setStatus(status);
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(title);
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
        return response[0].getId();
    }

    public static String searchYouTubeForSong(String search){
        final SearchResult[] searchResult = new SearchResult[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SearchListResponse searchListResponse = null;
                try {
                    searchListResponse = mService.search().list("snippet").setQ(search).setType("video").setVideoCategoryId("10").execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
}
