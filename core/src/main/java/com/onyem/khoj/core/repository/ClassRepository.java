package com.onyem.khoj.core.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Clazz;

public interface ClassRepository extends GraphRepository<Clazz> {

    @Query(value = "MATCH (clazz {nodeType:'com.onyem.khoj.core.domain.Clazz', name:{0}})-[:PACKAGE_HAS_CLASS]->(pkg {nodeType:'com.onyem.khoj.core.domain.Package', name:{1}}) return clazz")
    Clazz findByName(String className, String packageName);

    @Query(value = "MATCH (clazz {nodeType:'com.onyem.khoj.core.domain.Clazz'})-[:IMPLEMENTS]->(interface {nodeType:'com.onyem.khoj.core.domain.Clazz'}) WHERE id(clazz) = {0} return interface")
    Set<Clazz> findInterfacesImplementedByClazz(long clazzId);

}
