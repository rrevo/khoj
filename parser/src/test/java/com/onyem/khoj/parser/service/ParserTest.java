package com.onyem.khoj.parser.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
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

import com.onyem.khoj.core.domain.Access;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Flag;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.domain.Type;
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

    @SuppressWarnings("unchecked")
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
            Assert.assertEquals(Access.PUBLIC, clazzParserTest.getAccess());
            Assert.assertEquals(Type.CLASS, clazzParserTest.getType());
            Assert.assertTrue(clazzParserTest.getFlags().isEmpty());

            Package pkg = clazzParserTest.getPkg();
            pkgId = pkg.getId();
            Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());

            assertMethods(clazzParserTest.getMethods(), false, c2("graphDatabaseService", Access.DEFAULT),
                    c2("test", Access.PUBLIC), c2("<init>", Access.PUBLIC), c2("assertMethods", Access.PRIVATE),
                    c2("getMethodByName", Access.PRIVATE), c2("c", Access.DEFAULT), c2("c2", Access.PRIVATE),
                    c2("c3", Access.PROTECTED));
            Method dbServiceMethod = getMethodByName("graphDatabaseService", clazzParserTest.getMethods());
            Method assertMethodsMethod = getMethodByName("assertMethods", clazzParserTest.getMethods());

            className = "org.neo4j.test.TestGraphDatabaseFactory";
            Clazz clazzDatabaseFactory = classService.findByCanonicalName(className);
            Assert.assertNull(clazzDatabaseFactory.getAccess());
            Assert.assertNull(clazzDatabaseFactory.getType());
            Assert.assertNull(clazzDatabaseFactory.getFlags());

            assertMethods(clazzDatabaseFactory.getMethods(), true, c("newImpermanentDatabase"));
            Method methodInvoked = clazzDatabaseFactory.getMethods().iterator().next();

            Set<Method> methodsInvoked = classService.getMethodsInvoked(dbServiceMethod);
            Assert.assertEquals(1, methodsInvoked.size());
            Assert.assertEquals(methodInvoked.getId(), methodsInvoked.iterator().next().getId());

            methodsInvoked = classService.getMethodsInvoked(assertMethodsMethod);
            Assert.assertEquals(7, methodsInvoked.size());

            Clazz clazzMethod = classService.findByCanonicalName("com.onyem.khoj.core.domain.Method");
            Assert.assertNull(clazzMethod.getAccess());
            Assert.assertNull(clazzMethod.getType());
            Assert.assertNull(clazzMethod.getFlags());

            Assert.assertEquals(getMethodByName("getFlags", clazzMethod.getMethods()).getId(),
                    getMethodByName("getFlags", methodsInvoked).getId());
            Assert.assertEquals(getMethodByName("getAccess", clazzMethod.getMethods()).getId(),
                    getMethodByName("getAccess", methodsInvoked).getId());
        }
        {
            String className = "com.onyem.khoj.parser.service.impl.ClassParserServiceImpl";
            ClassReader classReader = new ClassReader(className);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(classWriter, 0);
            byte[] bytes = classWriter.toByteArray();

            Clazz clazz = classParserService.addClass(bytes);
            Assert.assertNotNull(clazz.getId());
            Assert.assertEquals("ClassParserServiceImpl", clazz.getName());
            Assert.assertEquals(Access.PUBLIC, clazz.getAccess());
            Assert.assertEquals(Type.CLASS, clazz.getType());

            Package pkg = clazz.getPkg();
            Assert.assertNotNull(pkg.getId());
            Assert.assertEquals("com.onyem.khoj.parser.service.impl", pkg.getName());

            assertMethods(clazz.getMethods(), false, c2("addClass", Access.PUBLIC), c2("getClassName", Access.PRIVATE),
                    c2("<init>", Access.PUBLIC), c2("findMethodByName", Access.PRIVATE),
                    c2("getPackageName", Access.PRIVATE));

            className = "com.onyem.khoj.parser.service.ClassParserService";
            classReader = new ClassReader(className);
            classWriter = new ClassWriter(0);
            classReader.accept(classWriter, 0);
            bytes = classWriter.toByteArray();

            clazz = classParserService.addClass(bytes);

            Assert.assertNotNull(clazz.getId());
            Assert.assertEquals("ClassParserService", clazz.getName());
            Assert.assertEquals(Access.PUBLIC, clazz.getAccess());
            Assert.assertEquals(Type.INTERFACE, clazz.getType());
            Assert.assertEquals(Collections.singleton(Flag.ABSTRACT), clazz.getFlags());

            pkg = clazz.getPkg();
            Assert.assertEquals("com.onyem.khoj.parser.service", pkg.getName());
            Assert.assertEquals(pkgId, pkg.getId().longValue());

            assertMethods(clazz.getMethods(), true, c3("addClass", Access.PUBLIC, Flag.ABSTRACT));

            clazz = classService.findByCanonicalName("com.onyem.khoj.core.domain.Clazz");
            Assert.assertNull(clazz.getAccess());
            Assert.assertNull(clazz.getType());
            Assert.assertNull(clazz.getFlags());

            assertMethods(clazz.getMethods(), true, c("getId"), c("getName"), c("getPkg"), c("getMethods"),
                    c("getAccess"), c("getType"), c("getFlags"), c("getCanonicalName"));
        }
    }

    Triple<String, Access, Flag> c(String name) {
        return c2(name, null);
    }

    private Triple<String, Access, Flag> c2(String name, Access access) {
        return c3(name, access, null);
    }

    protected Triple<String, Access, Flag> c3(String name, Access access, Flag flag) {
        return new ImmutableTriple<String, Access, Flag>(name, access, flag);
    }

    @SuppressWarnings("unchecked")
    private void assertMethods(Set<Method> methods, boolean checkSize, Triple<String, Access, Flag>... methodInfos) {
        Map<String, Method> methodsByName = methods.stream().collect(Collectors.toMap(Method::getName, (m) -> m));
        int size = 0;
        for (Triple<String, Access, Flag> methodInfo : methodInfos) {
            String name = methodInfo.getLeft();
            Method method = methodsByName.get(name);
            Assert.assertEquals("Checking method " + name, methodInfo.getMiddle(), method.getAccess());
            Flag flag = methodInfo.getRight();
            if (flag != null) {
                Assert.assertEquals(1, method.getFlags().size());
                Assert.assertEquals(flag, method.getFlags().iterator().next());
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
