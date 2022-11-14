package com.einundneunzig.animesongstoyoutube;

import android.net.Uri;

import java.net.URL;
import java.util.List;

public class Anime {
    private String title;
    private int id;
    private URL photoUrl;

    public Anime(int id, String title, URL photoUrl){
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

    public URL getPhotoUrl() {
        return photoUrl;
    }
}
