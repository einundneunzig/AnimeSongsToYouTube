package com.einundneunzig.animesongstoyoutube.myanimelist;


import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeContainer{
    private Node node;

    public NodeContainer(Node node) {
        this.node = node;
    }

    public NodeContainer() {
    }

    public Node getNode() {
        return node;
    }

    @JsonProperty("node")
    public void setNode(Node node) {
        this.node = node;
    }
}
