package com.onyem.khoj.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.service.ClassService;
import com.onyem.khoj.core.service.CoreModule;

@Configuration
@ComponentScan(basePackageClasses = CoreModule.class)
public class ClassTest {

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @Autowired
    ClassService classService;

    public void run() throws Exception {
        List<String> canonicalClassNames = Arrays.asList(new String[] { "Object", "String" });

        for (String clazzName : canonicalClassNames) {
            Package pkg = new Package();
            pkg.setName("java.lang");
            Clazz clazz = new Clazz();
            clazz.setName(clazzName);
            clazz.setPkg(pkg);
            clazz.addMethod(createHashCode());
            clazz.addMethod(createEquals());
            classService.addClass(clazz);
        }

        Clazz clazzByName = classService.findByCanonicalName("java.lang.Object");
        assertClass(clazzByName, "Object", "java.lang");

        clazzByName = classService.findByCanonicalName("java.lang.String");
        assertClass(clazzByName, "String", "java.lang");
    }

    private Method createHashCode() {
        Method method = new Method();
        method.setName("hashCode");
        return method;
    }

    private Method createEquals() {
        Method method = new Method();
        method.setName("equals");
        return method;
    }

    private void assertClass(Clazz clazzByName, String name, String pkg) {
        Assert.assertNotNull(clazzByName.getId());
        Assert.assertEquals(name, clazzByName.getName());
        final Long pkgId = clazzByName.getPkg().getId();
        Assert.assertNotNull(pkgId);
        Assert.assertEquals(pkg, clazzByName.getPkg().getName());

        Map<String, Method> methodsByName = clazzByName.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));

        Method method = methodsByName.get("hashCode");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
        method = methodsByName.get("equals");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
    }

    @Test
    public void testClass() throws Exception {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(ClassTest.class);
            ctx.refresh();

            ClassTest application = ctx.getBean(ClassTest.class);
            application.run();
        }
    }
}
