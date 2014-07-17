package com.onyem.khoj.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.onyem.khoj.core.domain.Artifact;
import com.onyem.khoj.parser.service.JarParserService;
import com.onyem.khoj.parser.service.ParserModule;

@Configuration
@ComponentScan(basePackageClasses = ParserModule.class)
public class Application {

    private static final int THRESHOLD = 10;

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("neo4j.db");
    }

    @Autowired
    JarParserService jarParserService;

    private final Logger logger = LoggerFactory.getLogger(Application.class);

    public void run() throws Exception {
        List<Path> jarPaths = getJarPaths();
        if (jarPaths.isEmpty()) {
            logger.warn("No Jars found for loading at gradle path: " + getGradlePath());
        }
        if (jarPaths.size() > THRESHOLD) {
            logger.warn("Ignoring some jars found on the gradle path: " + getGradlePath());
            jarPaths = jarPaths.subList(0, THRESHOLD);
        }
        for (Path jarPath : jarPaths) {
            Artifact artifact = getArtifact(jarPath);
            logger.info("Load start: " + artifact);
            jarParserService.addJar(artifact, jarPath.toFile());
            logger.info("Load complete: " + artifact);
        }
    }

    private Artifact getArtifact(Path path) {
        Artifact artifact = new Artifact();
        Path hashPath = path.getParent();

        Path versionPath = hashPath.getParent();
        artifact.setVersion(versionPath.getFileName().toString());

        Path artifactPath = versionPath.getParent();
        artifact.setArtifactId(artifactPath.getFileName().toString());

        Path groupPath = artifactPath.getParent();
        artifact.setGroupId(groupPath.getFileName().toString());

        return artifact;
    }

    private List<Path> getJarPaths() throws IOException {
        List<Path> jars = new ArrayList<Path>();

        try (DirectoryStream<Path> groupPaths = Files.newDirectoryStream(FileSystems.getDefault().getPath(
                getGradlePath()))) {
            for (Path groupPath : groupPaths) {

                try (DirectoryStream<Path> artifactPaths = Files.newDirectoryStream(groupPath)) {
                    for (Path artifactPath : artifactPaths) {

                        try (DirectoryStream<Path> versionPaths = Files.newDirectoryStream(artifactPath)) {
                            for (Path versionPath : versionPaths) {

                                try (DirectoryStream<Path> hashPaths = Files.newDirectoryStream(versionPath)) {
                                    for (Path hashPath : hashPaths) {

                                        try (DirectoryStream<Path> paths = Files.newDirectoryStream(hashPath)) {
                                            for (Path path : paths) {
                                                String filename = path.getFileName().toString();
                                                if (filename.endsWith(".jar") && !filename.endsWith("sources.jar")
                                                        && !filename.endsWith("tests.jar")) {
                                                    jars.add(path);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return jars;
    }

    private String getGradlePath() {
        return System.getProperty("user.home") + "/.gradle/caches/modules-2/files-2.1";
    }

    public static void main(String[] args) throws Exception {
        FileUtils.deleteRecursively(new File("neo4j.db"));

        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(Application.class);
            ctx.refresh();

            Application application = ctx.getBean(Application.class);
            application.run();
        }
    }

}
