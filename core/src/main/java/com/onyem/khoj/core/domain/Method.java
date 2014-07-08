package com.onyem.khoj.core.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Method {

    @GraphId
    private Long id;
    final String nodeType = "com.onyem.khoj.core.domain.Method";
    private String name;

    @Fetch
    @RelatedTo(type = "CLASS_HAS_METHODS")
    private Clazz clazz;

    private State state = State.TRANSIENT;

    private Access access;

    private Set<Flag> flags = new HashSet<Flag>();

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

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    public void addFlag(Flag flag) {
        this.flags.add(flag);
    }

    @Override
    public String toString() {
        return "Method [id=" + id + ", name=" + name + ", clazz=" + ((clazz == null) ? "null" : clazz.getId())
                + ", state=" + state + ", access=" + access + ", flags=" + flags + "]";
    }
}
