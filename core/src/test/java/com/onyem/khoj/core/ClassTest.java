package com.onyem.khoj.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.onyem.khoj.core.domain.Access;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
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
        assertClassAndMethods(clazzByName, "Object", "java.lang");

        clazzByName = classService.findByCanonicalName("java.lang.String");
        assertClassAndMethods(clazzByName, "String", "java.lang");
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
            Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
            classService.addClass(clazz);
            classService.addClassMethod(clazz, createEquals());
            classService.addClassMethod(clazz, createHashCode());
        }

        Clazz clazzByName = classService.findByCanonicalName("java.lang.Object");
        assertClassAndMethods(clazzByName, "Object", "java.lang");

        clazzByName = classService.findByCanonicalName("java.lang.String");
        assertClassAndMethods(clazzByName, "String", "java.lang");
    }

    @Test
    public void testClassNameSearch() throws Exception {
        Package pkg = new Package();
        pkg.setName("com.onyem");
        Clazz clazz = new Clazz();
        clazz.setPkg(pkg);
        clazz.setName("Foo");
        Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
        classService.addClass(clazz);

        Clazz clazzByName = classService.findByCanonicalName("com.onyem.Foo");
        assertClass(clazzByName, "Foo", "com.onyem");

        long classId = clazzByName.getId();

        pkg = new Package();
        pkg.setName("org.onyem");
        clazz = new Clazz();
        clazz.setPkg(pkg);
        clazz.setName("Foo");
        Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
        classService.addClass(clazz);

        clazzByName = classService.findByCanonicalName("org.onyem.Foo");
        assertClass(clazzByName, "Foo", "org.onyem");

        Assert.assertTrue(clazzByName.getId() != classId);
    }

    private Method createHashCode() {
        Method method = new Method();
        method.setName("hashCode");
        method.setAccess(Access.PUBLIC);
        return method;
    }

    private Method createEquals() {
        Method method = new Method();
        method.setName("equals");
        method.setAccess(Access.PUBLIC);
        return method;
    }

    private void assertClass(Clazz clazzByName, String name, String pkg) {
        Assert.assertNotNull(clazzByName.getId());
        Assert.assertEquals(name, clazzByName.getName());

        if (pkg == null) {
            Assert.assertNull(clazzByName.getPkg());
        } else {
            final Long pkgId = clazzByName.getPkg().getId();
            Assert.assertNotNull(pkgId);
            Assert.assertEquals(pkg, clazzByName.getPkg().getName());
        }
    }

    private void assertClassAndMethods(Clazz clazzByName, String name, String pkg) {
        assertClass(clazzByName, name, pkg);

        Map<String, Method> methodsByName = clazzByName.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));

        Method method = methodsByName.get("hashCode");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
        Assert.assertEquals(Access.PUBLIC, method.getAccess());
        Assert.assertTrue(method.getFlags().isEmpty());

        method = methodsByName.get("equals");
        Assert.assertNotNull(method.getId());
        Assert.assertEquals(clazzByName.getId(), method.getClazz().getId());
        Assert.assertEquals(Access.PUBLIC, method.getAccess());
        Assert.assertTrue(method.getFlags().isEmpty());
    }
}
