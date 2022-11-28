package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Node {
    private int id;
    private String title;
    private Picture main_picture;

    public Node(int id, String title, Picture main_picture) {
        this.id = id;
        this.title = title;
        this.main_picture = main_picture;
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
}
