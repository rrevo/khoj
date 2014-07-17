package com.onyem.khoj.core.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Artifact {

    @GraphId
    private Long id;
    final String nodeType = "com.onyem.khoj.core.domain.Artifact";

    private String groupId;
    private String artifactId;
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Artifact [id=" + id + ", groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
                + "]";
    }

}
