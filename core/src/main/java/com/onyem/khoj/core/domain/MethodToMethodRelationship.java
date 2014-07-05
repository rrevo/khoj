package com.onyem.khoj.core.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity
public class MethodToMethodRelationship {

    @GraphId
    private Long id;

    @StartNode
    private Method from;

    @EndNode
    private Method to;

    public Long getId() {
        return id;
    }

    public Method getFrom() {
        return from;
    }

    public Method getTo() {
        return to;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrom(Method from) {
        this.from = from;
    }

    public void setTo(Method to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "MethodToMethodRelationship [id=" + id + ", from=" + from + ", to=" + to + "]";
    }

}
