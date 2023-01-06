package com.einundneunzig.animesongstoyoutube.myanimelist.httpresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Theme {
    private int id;
    private int animeId;
    private String text;

    public Theme(int id, int animeId, String text) {
        this.id = id;
        this.animeId = animeId;
        this.text = text;
    }

    public Theme() {
    }

    public String getTitle(){
        String t = getFormattedText();
        return t.substring(0, t.indexOf("by")).trim();
    }

    public String getSinger(){
        String t = getFormattedText();
        return t.substring(t.indexOf("by")+3).trim();
    }

    public String getFormattedText(){
        return text.replace("\"", "").replaceAll("\\(eps.*?\\)", "").replace("  ", " ").trim();
    }
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    public int getAnimeId() {
        return animeId;
    }

    @JsonProperty("anime_id")
    public void setAnimeId(int animeId) {
        this.animeId = animeId;
    }

    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Theme theme = (Theme) o;
        return id == theme.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
