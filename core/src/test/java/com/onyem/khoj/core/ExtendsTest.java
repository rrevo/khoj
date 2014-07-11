package com.onyem.khoj.core;

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

public class ExtendsTest extends AbstractTestBase {

    @Autowired
    ClassService classService;

    @Test
    public void testExtends() throws Exception {

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

        Assert.assertNull(classService.getClassExtends(ideInterface));

        classService.addClassExtends(ideInterface, rcpInterface);

        Clazz superClazz = classService.getClassExtends(ideInterface);
        Assert.assertEquals(rcpInterface.getCanonicalName(), superClazz.getCanonicalName());
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
