package com.einundneunzig.animesongstoyoutube;

import android.net.Uri;

import java.util.List;

public class Anime {
    private String title;
    private int id;
    private Uri photoUrl;

    public Anime(int id, String title, Uri photoUrl){
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
    }

    public List<String> getOpeningThemes(){
        return null;
    }

    public List<String> getEndingThemes(){
        return null;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }
}
