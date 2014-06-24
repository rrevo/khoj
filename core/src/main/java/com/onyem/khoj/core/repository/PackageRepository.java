package com.onyem.khoj.core.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.onyem.khoj.core.domain.Package;

public interface PackageRepository extends GraphRepository<Package> {

    Package findByName(String name);

}
