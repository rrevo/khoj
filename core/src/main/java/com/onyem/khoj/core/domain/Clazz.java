package com.onyem.khoj.core.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Clazz {

    @GraphId
    private Long id;
    final String nodeType = "com.onyem.khoj.core.domain.Clazz";

    private String name;

    @Fetch
    @RelatedTo(type = "PACKAGE_HAS_CLASS", direction = Direction.BOTH)
    private Package pkg;

    @Fetch
    @RelatedTo(type = "CLASS_HAS_METHODS", direction = Direction.BOTH)
    private Set<Method> methods = new HashSet<>();

    private Type type;

    private Access access;

    private Set<Flag> flags;

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
        name = name.replace("/", ".");
        int lastDot = name.lastIndexOf(".");
        if (lastDot != -1) {
            String pkgName = name.substring(0, lastDot);
            Package pkg = new Package();
            pkg.setName(pkgName);
            setPkg(pkg);
            name = name.substring(lastDot + 1);
        }
        this.name = name;
    }

    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
        setMethodsClass(methods);
    }

    public void addMethod(Method method) {
        methods.add(method);
        setMethodsClass(Collections.singleton(method));
    }

    private void setMethodsClass(Collection<Method> methods) {
        for (Method method : methods) {
            method.setClazz(this);
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCanonicalName() {
        if (pkg == null) {
            return name;
        } else {
            return pkg.getName() + "." + getName();
        }
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    public void addFlag(Flag flag) {
        if (flags == null) {
            flags = new HashSet<Flag>();
        }
        flags.add(flag);
    }

    @Override
    public String toString() {
        return "Clazz [id=" + id + ", type=" + type + ", name=" + getCanonicalName() + ", access=" + access
                + ", methods=" + methods + ", state=" + state + ", flags=" + flags + "]";
    }

}
