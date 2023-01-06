package com.einundneunzig.animesongstoyoutube.myanimelist.httpresponse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.net.URL;

public class Picture {
    private String medium;
    private String large;

    public Picture(String medium, String large) {
        this.medium = medium;
        this.large = large;
    }

    public Picture() {
    }

    public Bitmap getMediumBitmap() {
        return getBitmap(medium);
    }

    public Bitmap getLargeBitmap(){
        return getBitmap(large);
    }

    private Bitmap getBitmap(String pictureUrl){
        final Bitmap[] bitmap = new Bitmap[1];
        Thread t = new Thread(() -> {
            try {
                bitmap[0] = BitmapFactory.decodeStream(new URL(pictureUrl).openStream());
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

        return bitmap[0];
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }

    @JsonProperty("medium")
    public void setMedium(String medium) {
        this.medium = medium;
    }


    @JsonProperty("large")
    public void setLarge(String large) {
        this.large = large;
    }
}
