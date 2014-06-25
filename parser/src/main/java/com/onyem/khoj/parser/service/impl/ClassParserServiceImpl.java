package com.onyem.khoj.parser.service.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.State;
import com.onyem.khoj.core.service.ClassService;
import com.onyem.khoj.parser.service.ClassParserService;

@Service
public class ClassParserServiceImpl implements ClassParserService {

    @Autowired
    ClassService classService;

    @Override
    public Clazz addClass(byte[] bytes) {
        ClassPrinter cp = new ClassPrinter(classService);
        ClassReader cr = new ClassReader(bytes);
        cr.accept(cp, 0);

        return cp.clazz;
    }

    static class ClassPrinter extends ClassVisitor {
        final private ClassService classService;

        private Clazz clazz = null;

        public ClassPrinter(ClassService classService) {
            super(Opcodes.ASM5);
            this.classService = classService;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            Clazz clazz = new Clazz();
            clazz.setName(name);
            clazz.setState(State.PARTIAL);
            this.clazz = classService.addClass(clazz);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Method method = new Method();
            method.setName(name);
            classService.addClassMethod(clazz, method);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            clazz.setState(State.COMPLETE);
            this.clazz = classService.addClass(clazz);
            super.visitEnd();
        }

    }

}
