package com.onyem.khoj.parser.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.service.ClassService;
import com.onyem.khoj.parser.service.ClassParserService;

@Service
public class ClassParserServiceImpl implements ClassParserService {

    @Autowired
    ClassService classService;

    @Override
    public Clazz addClass(byte[] bytes) {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader(bytes);
        cr.accept(cp, 0);

        Clazz clazz = new Clazz();
        clazz.setName(cp.name);
        for (String methodName : cp.methodNames) {
            Method method = new Method();
            method.setName(methodName);
            clazz.addMethod(method);
        }
        return classService.addClass(clazz);
    }

    static class ClassPrinter extends ClassVisitor {
        String name;
        List<String> methodNames = new ArrayList<>();

        public ClassPrinter() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.name = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            methodNames.add(name);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

}
