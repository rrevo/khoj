package com.onyem.khoj.core.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Clazz;

public interface ClassRepository extends GraphRepository<Clazz> {

    Clazz findByName(String name);

}
