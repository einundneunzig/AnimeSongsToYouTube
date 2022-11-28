package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    private int id;
    private String title;
    private Picture main_picture;
    private RelatedAnime[] related_anime;

    public Response(int id, String title, Picture main_picture, RelatedAnime[] related_anime) {
        this.id = id;
        this.title = title;
        this.main_picture = main_picture;
        this.related_anime = related_anime;
    }

    public Response() {
    }

    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public Picture getMain_picture() {
        return main_picture;
    }

    @JsonProperty("main_picture")
    public void setMain_picture(Picture main_picture) {
        this.main_picture = main_picture;
    }

    public RelatedAnime[] getRelatedAnime() {
        return related_anime;
    }

    @JsonProperty("related_anime")
    public void setRelatedAnime(RelatedAnime[] related_anime) {
        this.related_anime = related_anime;
    }
}
