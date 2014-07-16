package com.onyem.khoj.parser.service;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.onyem.khoj.core.domain.Access;
import com.onyem.khoj.core.domain.Artifact;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Type;
import com.onyem.khoj.core.service.ClassService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { ParserModule.class, JarParserTest.class })
@Configuration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JarParserTest {

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @Autowired
    JarParserService jarParserService;

    @Autowired
    ClassService classService;

    private static final String version = "3.3.1";
    private static final String artifactId = "commons-lang3";
    private static final String groupId = "org.apache.commons";

    @Test
    public void test() throws Exception {
        Artifact artifact = new Artifact();
        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactId);
        artifact.setVersion(version);

        artifact = jarParserService.addJar(artifact, new File(getCommonsLangJarPath()));
        Long artifactIdLong = artifact.getId();

        Assert.assertNotNull(artifactIdLong);
        Assert.assertEquals(groupId, artifact.getGroupId());
        Assert.assertEquals(artifactId, artifact.getArtifactId());
        Assert.assertEquals(version, artifact.getVersion());

        Clazz versionClazz = classService.findByCanonicalName("org.apache.commons.lang3.JavaVersion");

        Set<Clazz> classes = classService.getArtifactClasses(artifact);
        boolean found = false;
        for (Clazz clazz : classes) {
            if (versionClazz.getId().equals(clazz.getId())) {
                found = true;
            }
        }
        Assert.assertTrue(found);

        Assert.assertEquals(Type.ENUM, versionClazz.getType());
        Assert.assertEquals(Access.PUBLIC, versionClazz.getAccess());

        List<Method> methods = versionClazz.getMethods().stream().filter(m -> m.getName().equals("atLeast"))
                .collect(Collectors.toList());
        Assert.assertEquals(1, methods.size());

        Clazz enumClazz = classService.getClassExtends(versionClazz);
        Assert.assertEquals("java.lang.Enum", enumClazz.getCanonicalName());
    }

    private String getCommonsLangJarPath() {
        return getMavenRepoFolder() + "org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar";
    }

    private String getMavenRepoFolder() {
        return System.getProperty("user.home") + "/.m2/repository/";
    }
}
