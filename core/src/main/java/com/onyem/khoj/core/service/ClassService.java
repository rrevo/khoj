package com.onyem.khoj.core.service;

import java.util.Set;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;

public interface ClassService {

    Clazz addClass(Clazz clazz);

    Clazz addClassMethod(Clazz clazz, Method method);

    Clazz findByCanonicalName(String name);

    boolean addMethodInvokes(Method source, Method destination);

    Set<Method> getMethodsInvoked(Method source);

}
