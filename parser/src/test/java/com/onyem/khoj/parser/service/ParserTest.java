package com.onyem.khoj.parser.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.domain.State;
import com.onyem.khoj.core.service.ClassService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { ParserModule.class, ParserTest.class })
@Configuration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ParserTest {

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @Autowired
    ClassParserService classParserService;

    @Autowired
    ClassService classService;

    @Test
    public void test() throws Exception {
        String className = "com.onyem.khoj.parser.service.ParserTest";
        ClassReader classReader = new ClassReader(className);
        ClassWriter classWriter = new ClassWriter(0);
        classReader.accept(classWriter, 0);
        byte[] bytes = classWriter.toByteArray();

        Clazz clazz = classParserService.addClass(bytes);
        Assert.assertNotNull(clazz.getId());
        Assert.assertEquals("ParserTest", clazz.getName());

        Package pkg = clazz.getPkg();
        final long pkgId = pkg.getId();
        Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());

        assertMethods(clazz.getMethods(), State.COMPLETE, false, "graphDatabaseService", "test", "<init>");

        className = "org.neo4j.test.TestGraphDatabaseFactory";
        clazz = classService.findByCanonicalName(className);
        Assert.assertEquals(State.INFERRED, clazz.getState());
        assertMethods(clazz.getMethods(), State.INFERRED, true, "newImpermanentDatabase");

        className = "com.onyem.khoj.parser.service.ClassParserService";
        classReader = new ClassReader(className);
        classWriter = new ClassWriter(0);
        classReader.accept(classWriter, 0);
        bytes = classWriter.toByteArray();

        clazz = classParserService.addClass(bytes);
        Assert.assertNotNull(clazz.getId());
        Assert.assertEquals("ClassParserService", clazz.getName());

        pkg = clazz.getPkg();
        Assert.assertEquals(pkgId, pkg.getId().longValue());
        Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());

        assertMethods(clazz.getMethods(), State.COMPLETE, true, "addClass");

        clazz = classService.findByCanonicalName("com.onyem.khoj.core.domain.Clazz");
        Assert.assertEquals(State.INFERRED, clazz.getState());
        assertMethods(clazz.getMethods(), State.INFERRED, true, "getId", "getName", "getPkg", "getMethods", "getState");
    }

    private void assertMethods(Set<Method> methods, State state, boolean checkSize, String... names) {
        Map<String, Method> methodsByName = methods.stream().collect(Collectors.toMap(Method::getName, (m) -> m));
        int size = 0;
        for (String name : names) {
            Assert.assertTrue(methodsByName.containsKey(name));
            if (state != null) {
                Assert.assertEquals(state, methodsByName.get(name).getState());
            }
            size++;
        }
        if (checkSize) {
            Assert.assertEquals(size, methodsByName.size());
        }
    }
}
