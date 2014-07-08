package com.onyem.khoj.core.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Method;

public interface MethodRepository extends GraphRepository<Method> {

    @Query(value = "MATCH (method {nodeType:'com.onyem.khoj.core.domain.Method'})-[:INVOKES]->(invoked {nodeType:'com.onyem.khoj.core.domain.Method'}) WHERE id(method) = {0} return (invoked)")
    Set<Method> findMethodsByInvoked(long methodId);

}
