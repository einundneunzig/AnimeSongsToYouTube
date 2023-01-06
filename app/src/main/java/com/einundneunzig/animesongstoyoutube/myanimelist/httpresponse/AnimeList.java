package com.einundneunzig.animesongstoyoutube.myanimelist.httpresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimeList{
        private NodeContainer[] data;
        private Paging paging;

    public AnimeList(NodeContainer[] data, Paging paging) {
        this.data = data;
        this.paging = paging;
    }

    public AnimeList() {
    }

    public Node[] getData() {
        Node[] nodes = new Node[data.length];
        for(int i = 0; i<data.length; i++){
            nodes[i] = data[i].getNode();
        }
        return nodes;
    }


    @JsonProperty("data")
    public void setData(NodeContainer[] data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    @JsonProperty("paging")
    public void setPaging(Paging paging) {
        this.paging = paging;
    }

}
