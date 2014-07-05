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

public class MethodInvokeTest extends AbstractTestBase {

    @Autowired
    ClassService classService;

    @Test
    public void testMethodInvocation() throws Exception {

        List<String> canonicalClassNames = Arrays.asList(new String[] { "Ben", "Jerry" });

        for (String clazzName : canonicalClassNames) {
            Package pkg = new Package();
            pkg.setName("com.onyem");

            Clazz clazz = new Clazz();
            clazz.setName(clazzName);
            clazz.setPkg(pkg);
            clazz.addMethod(createMethod("foo"));
            clazz.addMethod(createMethod("bar"));
            clazz.addMethod(createMethod("baz"));
            clazz.setState(State.PARTIAL);
            Assert.assertNull(classService.findByCanonicalName(clazz.getCanonicalName()));
            classService.addClass(clazz);
        }

        Clazz clazzBen = classService.findByCanonicalName("com.onyem.Ben");
        Assert.assertNotNull(clazzBen.getId());
        Map<String, Method> methodsBen = clazzBen.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));
        Method methodBenFoo = methodsBen.get("foo");
        Method methodBenBaz = methodsBen.get("baz");
        Assert.assertNotNull(methodBenFoo.getId());
        Assert.assertNotNull(methodBenBaz.getId());

        Clazz clazzJerry = classService.findByCanonicalName("com.onyem.Jerry");
        Assert.assertNotNull(clazzJerry.getId());
        Map<String, Method> methodsJerry = clazzBen.getMethods().stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));
        Method methodJerryFoo = methodsJerry.get("foo");
        Method methodJerryBar = methodsJerry.get("bar");
        Assert.assertNotNull(methodJerryFoo.getId());
        Assert.assertNotNull(methodJerryBar.getId());

        Assert.assertTrue(classService.addMethodInvokes(methodBenFoo, methodBenBaz));
        Assert.assertTrue(classService.addMethodInvokes(methodBenFoo, methodJerryFoo));
        Assert.assertTrue(classService.addMethodInvokes(methodBenFoo, methodJerryBar));

        Map<String, Method> methodsInvokedByFoo = classService.getMethodsInvoked(methodsBen.get("foo")).stream()
                .collect(Collectors.toMap(Method::getName, (m) -> m));
        Assert.assertEquals(3, methodsInvokedByFoo.size());
        assertEquals(methodBenBaz, methodsInvokedByFoo.get("baz"));
        assertEquals(methodJerryFoo, methodsInvokedByFoo.get("foo"));
        assertEquals(methodJerryBar, methodsInvokedByFoo.get("bar"));
    }

    private void assertEquals(Method expectedMethod, Method actualMethod) {
        Assert.assertEquals(expectedMethod.getId(), actualMethod.getId());
        Assert.assertEquals(expectedMethod.getName(), actualMethod.getName());
        assertEquals(expectedMethod.getClazz(), actualMethod.getClazz());
    }

    private void assertEquals(Clazz expected, Clazz actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getCanonicalName(), actual.getCanonicalName());
    }

    private Method createMethod(String name) {
        Method method = new Method();
        method.setName(name);
        return method;
    }
}
