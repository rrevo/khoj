package com.onyem.khoj.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.onyem.khoj.core.domain.Artifact;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.service.ClassService;

public class ArtifactTest extends AbstractTestBase {

    private static final String version = "1.0";
    private static final String artifactId = "khoj";
    private static final String groupId = "com.onyem";

    @Autowired
    ClassService classService;

    @Test
    public void testArtifacts() throws Exception {

        Artifact artifact = new Artifact();
        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactId);
        artifact.setVersion(version);
        artifact = classService.addArtifact(artifact);

        Long artifactIdLong = artifact.getId();

        Assert.assertNotNull(artifactIdLong);
        Assert.assertEquals(groupId, artifact.getGroupId());
        Assert.assertEquals(artifactId, artifact.getArtifactId());
        Assert.assertEquals(version, artifact.getVersion());

        Set<Artifact> foundArtifacts = classService.findByGroupAndArtifact(groupId, artifactId);
        Assert.assertEquals(1, foundArtifacts.size());
        Assert.assertEquals(artifactIdLong, foundArtifacts.iterator().next().getId());

        List<String> canonicalClassNames = Arrays.asList(new String[] { "Ben", "Jerry" });

        for (String clazzName : canonicalClassNames) {
            Package pkg = new Package();
            pkg.setName(groupId);

            Clazz clazz = new Clazz();
            clazz.setName(clazzName);
            clazz.setPkg(pkg);
            Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
            classService.addClass(clazz);

            Assert.assertTrue(classService.addArtifactContainsClasses(artifact, Collections.singleton(clazz)));
        }

        Set<Clazz> classes = classService.getArtifactClasses(artifact);
        Assert.assertEquals(2, classes.size());
        System.out.println("ArtifactTest.testArtifacts() " + classes);
        Map<String, Clazz> classesByName = classes.stream()
                .collect(Collectors.toMap(Clazz::getCanonicalName, (c) -> c));

        Clazz clazzBen = classesByName.get("com.onyem.Ben");
        Assert.assertNotNull(clazzBen.getId());

        Clazz clazzJerry = classesByName.get("com.onyem.Jerry");
        Assert.assertNotNull(clazzJerry.getId());
    }
}
