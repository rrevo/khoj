package com.onyem.khoj.core.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

/**
 * NOTE An external GraphDatabaseService needs to be present
 * 
 */
@Configuration
@ComponentScan("com.onyem.khoj.core.service.impl")
@EnableNeo4jRepositories(basePackages = "com.onyem.khoj.core")
public class CoreModule extends Neo4jConfiguration {

    public CoreModule() {
        setBasePackage("com.onyem.khoj.core");
    }

}
