package com.onyem.khoj.parser.service;

import java.util.Collection;
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
        long pkgId = -1;
        {
            String className = "com.onyem.khoj.parser.service.ParserTest";
            ClassReader classReader = new ClassReader(className);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(classWriter, 0);
            byte[] bytes = classWriter.toByteArray();

            Clazz clazzParserTest = classParserService.addClass(bytes);
            Assert.assertNotNull(clazzParserTest.getId());
            Assert.assertEquals("ParserTest", clazzParserTest.getName());

            Package pkg = clazzParserTest.getPkg();
            pkgId = pkg.getId();
            Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());

            assertMethods(clazzParserTest.getMethods(), State.COMPLETE, false, "graphDatabaseService", "test",
                    "<init>", "assertMethods", "getMethodByName");
            Method dbServiceMethod = getMethodByName("graphDatabaseService", clazzParserTest.getMethods());
            Method assertMethodsMethod = getMethodByName("assertMethods", clazzParserTest.getMethods());

            className = "org.neo4j.test.TestGraphDatabaseFactory";
            Clazz clazzDatabaseFactory = classService.findByCanonicalName(className);
            Assert.assertEquals(State.INFERRED, clazzDatabaseFactory.getState());
            assertMethods(clazzDatabaseFactory.getMethods(), State.INFERRED, true, "newImpermanentDatabase");
            Method methodInvoked = clazzDatabaseFactory.getMethods().iterator().next();

            Set<Method> methodsInvoked = classService.getMethodsInvoked(dbServiceMethod);
            Assert.assertEquals(1, methodsInvoked.size());
            Assert.assertEquals(methodInvoked.getId(), methodsInvoked.iterator().next().getId());

            methodsInvoked = classService.getMethodsInvoked(assertMethodsMethod);
            Assert.assertEquals(2, methodsInvoked.size());
            Clazz clazzMethod = classService.findByCanonicalName("com.onyem.khoj.core.domain.Method");
            Assert.assertEquals(getMethodByName("getState", clazzMethod.getMethods()).getId(),
                    getMethodByName("getState", methodsInvoked).getId());
            Assert.assertEquals(getMethodByName("getId", clazzMethod.getMethods()).getId(),
                    getMethodByName("getId", methodsInvoked).getId());
        }
        {
            String className = "com.onyem.khoj.parser.service.ClassParserService";
            ClassReader classReader = new ClassReader(className);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(classWriter, 0);
            byte[] bytes = classWriter.toByteArray();

            Clazz clazz = classParserService.addClass(bytes);
            Assert.assertNotNull(clazz.getId());
            Assert.assertEquals("ClassParserService", clazz.getName());

            Package pkg = clazz.getPkg();
            Assert.assertEquals(pkgId, pkg.getId().longValue());
            Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());

            assertMethods(clazz.getMethods(), State.COMPLETE, true, "addClass");

            clazz = classService.findByCanonicalName("com.onyem.khoj.core.domain.Clazz");
            Assert.assertEquals(State.INFERRED, clazz.getState());
            assertMethods(clazz.getMethods(), State.INFERRED, true, "getId", "getName", "getPkg", "getMethods",
                    "getState");
        }
    }

    private void assertMethods(Set<Method> methods, State state, boolean checkSize, String... names) {
        Map<String, Method> methodsByName = methods.stream().collect(Collectors.toMap(Method::getName, (m) -> m));
        int size = 0;
        for (String name : names) {
            Assert.assertTrue(methodsByName.containsKey(name));
            if (state != null) {
                Assert.assertEquals(state, methodsByName.get(name).getState());
                Assert.assertNotNull(methodsByName.get(name).getId());
            }
            size++;
        }
        if (checkSize) {
            Assert.assertEquals(size, methodsByName.size());
        }
    }

    private Method getMethodByName(String name, Collection<Method> methods) {
        return methods.stream().filter(m -> m.getName().equals(name)).findFirst().get();
    }
}
