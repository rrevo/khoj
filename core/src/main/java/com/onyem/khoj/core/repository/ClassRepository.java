package com.onyem.khoj.core.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Clazz;

public interface ClassRepository extends GraphRepository<Clazz> {

    @Query(value = "MATCH (clazz {type:'com.onyem.khoj.core.domain.Clazz', name:{0}})-[:PACKAGE_HAS_CLASS]->(pkg {type:'com.onyem.khoj.core.domain.Package', name:{1}}) return clazz")
    Clazz findByName(String className, String packageName);

}
