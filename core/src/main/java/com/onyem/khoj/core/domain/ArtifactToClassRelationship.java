package com.onyem.khoj.core.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity
public class ArtifactToClassRelationship {

    @GraphId
    private Long id;

    @StartNode
    private Artifact from;

    @EndNode
    private Clazz to;

    public Long getId() {
        return id;
    }

    public Artifact getFrom() {
        return from;
    }

    public Clazz getTo() {
        return to;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrom(Artifact from) {
        this.from = from;
    }

    public void setTo(Clazz to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "ClassToClassRelationship [id=" + id + ", from=" + from + ", to=" + to + "]";
    }

}
