package com.onyem.khoj.parser.service.impl;

import java.util.Optional;

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
            Clazz newClass = classService.addClassMethod(clazz, method);
            method = findMethodByName(newClass, name).get();
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            MethodPrinter methodPrinter = new MethodPrinter(api, methodVisitor, classService, method);
            return methodPrinter;
        }

        @Override
        public void visitEnd() {
            clazz.setState(State.COMPLETE);
            this.clazz = classService.addClass(clazz);
            super.visitEnd();
        }

    }

    static class MethodPrinter extends MethodVisitor {
        final private ClassService classService;

        final private Method method;

        public MethodPrinter(int api, MethodVisitor methodVisitor, ClassService classService, Method method) {
            super(api, methodVisitor);
            this.classService = classService;
            this.method = method;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == Opcodes.INVOKEVIRTUAL) {

                Clazz clazz = new Clazz();
                clazz.setName(owner);
                clazz.setState(State.INFERRED);

                Clazz foundClazz = classService.findByCanonicalName(clazz.getCanonicalName());
                if (foundClazz == null) {
                    clazz = classService.addClass(clazz);
                } else {
                    clazz = foundClazz;
                }

                Optional<Method> isMethod = findMethodByName(clazz, name);
                Method invokedMethod = null;
                if (!isMethod.isPresent()) {
                    invokedMethod = new Method();
                    invokedMethod.setClazz(clazz);
                    invokedMethod.setName(name);
                    invokedMethod.setState(State.INFERRED);
                    clazz = classService.addClassMethod(clazz, invokedMethod);

                    invokedMethod = findMethodByName(clazz, name).get();
                } else {
                    invokedMethod = isMethod.get();
                }
                classService.addMethodInvokes(method, invokedMethod);

            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    static private Optional<Method> findMethodByName(Clazz clazz, String methodName) {
        return clazz.getMethods().stream().filter(m -> m.getName().equals(methodName)).findFirst();
    }

}
