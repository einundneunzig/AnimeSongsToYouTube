package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedAnime {
    private Node node;
    private String relation_type;
    private String relation_type_formatted;

    public RelatedAnime(Node node, String relation_type, String relation_type_formatted) {
        this.node = node;
        this.relation_type = relation_type;
        this.relation_type_formatted = relation_type_formatted;
    }

    public RelatedAnime() {
    }

    public Node getNode() {
        return node;
    }

    public String getRelation_type() {
        return relation_type;
    }

    public String getRelation_type_formatted() {
        return relation_type_formatted;
    }


    @JsonProperty("node")
    public void setNode(Node node) {
        this.node = node;
    }

    @JsonProperty("relation_type")
    public void setRelation_type(String relation_type) {
        this.relation_type = relation_type;
    }

    @JsonProperty("relation_type_formatted")
    public void setRelation_type_formatted(String relation_type_formatted) {
        this.relation_type_formatted = relation_type_formatted;
    }
}
