package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paging {
    private String previous;
    private String next;

    public Paging(String previous, String next) {
        this.previous = previous;
        this.next = next;
    }

    public Paging() {
    }

    public String getPrevious() {
        return previous;
    }

    @JsonProperty("previous")
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    @JsonProperty("next")
    public void setNext(String next) {
        this.next = next;
    }
}
