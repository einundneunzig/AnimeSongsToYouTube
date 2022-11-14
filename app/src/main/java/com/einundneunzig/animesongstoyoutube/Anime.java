package com.einundneunzig.animesongstoyoutube;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class Anime implements Serializable{
    private String title;
    private int id;
    private URL imageUrl;

    public Anime(int id, String title, URL imageUrl){
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
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

    public Bitmap getImage() {
        final Bitmap[] bitmap = new Bitmap[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap[0] = BitmapFactory.decodeStream(imageUrl.openStream());
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

        return bitmap[0];
    }

}
