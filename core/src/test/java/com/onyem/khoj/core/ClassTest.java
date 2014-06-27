package com.onyem.khoj.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.domain.State;
import com.onyem.khoj.core.service.ClassService;

public class ClassTest extends AbstractTestBase {

    @Autowired
    ClassService classService;

    @Test
    public void testBasicClassPackageAndMethods() throws Exception {
        List<String> canonicalClassNames = Arrays.asList(new String[] { "Object", "String" });

        for (String clazzName : canonicalClassNames) {
            Package pkg = new Package();
            pkg.setName("java.lang");
            Clazz clazz = new Clazz();
            clazz.setName(clazzName);
            clazz.setPkg(pkg);
            clazz.addMethod(createHashCode());
            clazz.addMethod(createEquals());
            Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
            classService.addClass(clazz);
        }

        Clazz clazzByName = classService.findByCanonicalName("java.lang.Object");
        assertClassAndMethods(clazzByName, "Object", "java.lang", State.COMPLETE);

        clazzByName = classService.findByCanonicalName("java.lang.String");
        assertClassAndMethods(clazzByName, "String", "java.lang", State.COMPLETE);
    }

    @Test
    public void testPartialClassPackageAndMethods() throws Exception {
        List<String> canonicalClassNames = Arrays.asList(new String[] { "Object", "String" });

        for (String clazzName : canonicalClassNames) {
            Package pkg = new Package();
            pkg.setName("java.lang");
            Clazz clazz = new Clazz();
            clazz.setName(clazzName);
            clazz.setPkg(pkg);
            clazz.addMethod(createHashCode());
            clazz.addMethod(createEquals());
            clazz.setState(State.PARTIAL);
            Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
            classService.addClass(clazz);
        }

        Clazz clazzByName = classService.findByCanonicalName("java.lang.Object");
        assertClassAndMethods(clazzByName, "Object", "java.lang", State.PARTIAL);
        clazzByName.setState(State.COMPLETE);
        clazzByName = classService.addClass(clazzByName);
        assertClassAndMethods(clazzByName, "Object", "java.lang", State.COMPLETE);

        clazzByName = classService.findByCanonicalName("java.lang.String");
        assertClassAndMethods(clazzByName, "String", "java.lang", State.PARTIAL);
        clazzByName.setState(State.COMPLETE);
        clazzByName = classService.addClass(clazzByName);
        assertClassAndMethods(clazzByName, "String", "java.lang", State.COMPLETE);
    }

    @Test
    public void testClassNameSearch() throws Exception {
        Package pkg = new Package();
        pkg.setName("com.onyem");
        Clazz clazz = new Clazz();
        clazz.setPkg(pkg);
        clazz.setName("Foo");
        clazz.setState(State.COMPLETE);
        Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
        classService.addClass(clazz);

        Clazz clazzByName = classService.findByCanonicalName("com.onyem.Foo");
        assertClass(clazzByName, "Foo", "com.onyem", State.COMPLETE);

        long classId = clazzByName.getId();

        pkg = new Package();
        pkg.setName("org.onyem");
        clazz = new Clazz();
        clazz.setPkg(pkg);
        clazz.setName("Foo");
        clazz.setState(State.COMPLETE);
        Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
        classService.addClass(clazz);

        clazzByName = classService.findByCanonicalName("org.onyem.Foo");
        assertClass(clazzByName, "Foo", "org.onyem", State.COMPLETE);

        Assert.assertTrue(clazzByName.getId() != classId);
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

    private void assertClass(Clazz clazzByName, String name, String pkg, State classState) {
        Assert.assertNotNull(clazzByName.getId());
        Assert.assertEquals(name, clazzByName.getName());
        Assert.assertEquals(classState, clazzByName.getState());

        if (pkg == null) {
            Assert.assertNull(clazzByName.getPkg());
        } else {
            final Long pkgId = clazzByName.getPkg().getId();
            Assert.assertNotNull(pkgId);
            Assert.assertEquals(pkg, clazzByName.getPkg().getName());
            Assert.assertEquals(State.COMPLETE, clazzByName.getPkg().getState());
        }
    }

    private void assertClassAndMethods(Clazz clazzByName, String name, String pkg, State classState) {
        assertClass(clazzByName, name, pkg, classState);

        Map<String, Method> methodsByName = clazzByName.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));

        Method method = methodsByName.get("hashCode");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
        Assert.assertEquals(State.COMPLETE, method.getState());

        method = methodsByName.get("equals");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
        Assert.assertEquals(State.COMPLETE, method.getState());
    }
}
