package com.onyem.khoj.core.domain;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Method {

    @GraphId
    private Long id;
    private String name;

    @Fetch
    @RelatedTo(type = "CLASS_HAS_METHODS")
    private Clazz clazz;

    private State state = State.TRANSIENT;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Method [id=" + id + ", name=" + name + ", clazz=" + clazz.getId() + ", state=" + state + "]";
    }

}
