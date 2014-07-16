package com.onyem.khoj.core.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Artifact;
import com.onyem.khoj.core.domain.Clazz;

public interface ArtifactRepository extends GraphRepository<Artifact> {

    @Query(value = "MATCH (artifact {nodeType:'com.onyem.khoj.core.domain.Artifact'}) WHERE artifact.groupId = {0} and artifact.artifactId = {1} return (artifact)")
    Set<Artifact> findByGroupAndArtifact(String groupId, String artifactId);

    @Query(value = "MATCH (artifact {nodeType:'com.onyem.khoj.core.domain.Artifact'})-[:PACKAGES]->(clazz {nodeType:'com.onyem.khoj.core.domain.Clazz'}) WHERE id(artifact) = {0} return clazz")
    Set<Clazz> getArtifactClasses(long id);
}
