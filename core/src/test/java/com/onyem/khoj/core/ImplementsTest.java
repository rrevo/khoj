package com.onyem.khoj.core;

import java.util.Arrays;
import java.util.HashSet;
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
import com.onyem.khoj.core.domain.Type;
import com.onyem.khoj.core.service.ClassService;

public class ImplementsTest extends AbstractTestBase {

    @Autowired
    ClassService classService;

    @Test
    public void testMethodAccess() throws Exception {

        Package pkg = new Package();
        pkg.setName("com.onyem");

        Clazz ideInterface = new Clazz();
        ideInterface.setName("Ide");
        ideInterface.setPkg(pkg);
        ideInterface.setAccess(Access.PUBLIC);
        ideInterface.setType(Type.INTERFACE);
        ideInterface.addFlag(Flag.ABSTRACT);
        ideInterface.addMethod(createMethod("foo", Access.DEFAULT, Flag.ABSTRACT));
        ideInterface = classService.addClass(ideInterface);

        Clazz rcpInterface = new Clazz();
        rcpInterface.setName("Rcp");
        rcpInterface.setPkg(pkg);
        rcpInterface.setAccess(Access.PUBLIC);
        rcpInterface.setType(Type.INTERFACE);
        rcpInterface.addFlag(Flag.ABSTRACT);
        rcpInterface.addMethod(createMethod("bar", Access.DEFAULT, Flag.ABSTRACT));
        rcpInterface = classService.addClass(rcpInterface);

        Clazz eclipseClass = new Clazz();
        eclipseClass.setName("Eclipse");
        eclipseClass.setPkg(pkg);
        eclipseClass.setAccess(Access.PUBLIC);
        eclipseClass.setType(Type.CLASS);
        eclipseClass.addMethod(createMethod("foo", Access.DEFAULT));
        eclipseClass.addMethod(createMethod("bar", Access.DEFAULT));
        eclipseClass = classService.addClass(eclipseClass);

        classService.addClassImplements(eclipseClass, new HashSet<Clazz>(Arrays.asList(ideInterface, rcpInterface)));

        Map<Long, Clazz> interfaces = classService.getClassImplements(eclipseClass).stream()
                .collect(Collectors.toMap(Clazz::getId, (m) -> m));
        Assert.assertEquals(2, interfaces.size());
        Assert.assertEquals(ideInterface.getCanonicalName(), interfaces.get(ideInterface.getId()).getCanonicalName());
        Assert.assertEquals(rcpInterface.getCanonicalName(), interfaces.get(rcpInterface.getId()).getCanonicalName());
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
