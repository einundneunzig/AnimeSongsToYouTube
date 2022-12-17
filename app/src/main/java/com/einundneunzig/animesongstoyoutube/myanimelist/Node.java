package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.einundneunzig.animesongstoyoutube.MyAnimeListManager;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Node {
    private int id;
    private String title;
    private Picture main_picture;
    private RelatedAnime[] relatedAnime;
    private Theme[] openingThemes;
    private Theme[] endingThemes;

    public Node(int id, String title, Picture main_picture, RelatedAnime[] relatedAnime, Theme[] openingThemes, Theme[] endingThemes) {
        this.id = id;
        this.title = title;
        this.main_picture = main_picture;
        this.relatedAnime = relatedAnime;
        this.openingThemes = openingThemes;
        this.endingThemes = endingThemes;
    }

    public Node() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Picture getMain_picture() {
        return main_picture;
    }

    public RelatedAnime[] getRelatedAnime() {return relatedAnime;}

    public Theme[] getOpeningThemes(){
        return openingThemes;
    }

    public Theme[] getEndingThemes() {
        return endingThemes;
    }

    public void loadDetails(){
        Node n = MyAnimeListManager.getAnimeDetails(id);
        setRelatedAnime(n.getRelatedAnime());
        setOpeningThemes(n.getOpeningThemes());
        setEndingThemes(n.getEndingThemes());

    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("main_picture")
    public void setMain_picture(Picture main_picture) {
        this.main_picture = main_picture;
    }

    @JsonProperty("related_anime")
    public void setRelatedAnime(RelatedAnime[] relatedAnime){
        this.relatedAnime = relatedAnime;
    }

    @JsonProperty("opening_themes")
    public void setOpeningThemes(Theme[] openingThemes){
        this.openingThemes = openingThemes;
    }

    @JsonProperty("ending_themes")
    public void setEndingThemes(Theme[] endingThemes){
        this.endingThemes = endingThemes;
    }
}
