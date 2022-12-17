package com.einundneunzig.animesongstoyoutube.myanimelist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedAnime {
    private Node node;
    private String relationType;
    private String relationTypeFormatted;

    public RelatedAnime(Node node, String relationType, String relationTypeFormatted) {
        this.node = node;
        this.relationType = relationType;
        this.relationTypeFormatted = relationTypeFormatted;
    }

    public RelatedAnime() {
    }

    public Node getNode() {
        return node;
    }

    public String getRelationType() {
        return relationType;
    }

    public String getRelationTypeFormatted() {
        return relationTypeFormatted;
    }


    @JsonProperty("node")
    public void setNode(Node node) {
        this.node = node;
    }

    @JsonProperty("relation_type")
    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    @JsonProperty("relation_type_formatted")
    public void setRelationTypeFormatted(String relationTypeFormatted) {
        this.relationTypeFormatted = relationTypeFormatted;
    }
}
