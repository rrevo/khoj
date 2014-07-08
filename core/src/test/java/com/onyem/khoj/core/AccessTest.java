package com.onyem.khoj.core;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.onyem.khoj.core.domain.Access;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Flag;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.service.ClassService;

public class AccessTest extends AbstractTestBase {

    @Autowired
    ClassService classService;

    @Test
    public void testMethodAccess() throws Exception {

        Package pkg = new Package();
        pkg.setName("com.onyem");

        Clazz clazz = new Clazz();
        clazz.setName("Eclipse");
        clazz.setPkg(pkg);
        clazz.setAccess(Access.PROTECTED);
        clazz.addFlag(Flag.ABSTRACT);
        clazz.addMethod(createMethod("foo", Access.PRIVATE));
        clazz.addMethod(createMethod("bar", null, Flag.FINAL));
        clazz.addMethod(createMethod("baz", Access.DEFAULT, Flag.ABSTRACT, Flag.FINAL));
        Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
        classService.addClass(clazz);

        clazz = classService.findByCanonicalName("com.onyem.Eclipse");
        Assert.assertNotNull(clazz.getId());
        Map<String, Method> methodsBen = clazz.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));
        Method method = methodsBen.get("foo");
        assertMethod(clazz, method, Access.PRIVATE);

        method = methodsBen.get("bar");
        assertMethod(clazz, method, null, Flag.FINAL);

        method = methodsBen.get("baz");
        assertMethod(clazz, method, Access.DEFAULT, Flag.ABSTRACT, Flag.FINAL);
    }

    private void assertMethod(Clazz expectedClazz, Method actual, Access access, Flag... flags) {
        Assert.assertNotNull(actual.getId());
        Assert.assertEquals(access, actual.getAccess());
        Assert.assertEquals(flags.length, actual.getFlags().size());
        for (Flag flag : flags) {
            Assert.assertTrue(actual.getFlags().contains(flag));
        }
        assertEquals(expectedClazz, actual.getClazz());
    }

    private void assertEquals(Clazz expected, Clazz actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getCanonicalName(), actual.getCanonicalName());
        Assert.assertEquals(expected.getAccess(), actual.getAccess());
        Assert.assertEquals(expected.getFlags(), actual.getFlags());
    }

    private Method createMethod(String name, Access access, Flag... flags) {
        Method method = new Method();
        method.setName(name);
        if (access != null) {
            method.setAccess(access);
        }
        for (Flag flag : flags) {
            method.addFlag(flag);
        }
        return method;
    }
}
