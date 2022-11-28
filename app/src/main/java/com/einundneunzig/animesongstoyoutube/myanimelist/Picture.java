package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Picture {
    private String medium;
    private String large;

    public Picture(String medium, String large) {
        this.medium = medium;
        this.large = large;
    }

    public Picture() {
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
