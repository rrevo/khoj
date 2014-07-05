package com.onyem.khoj.core.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Method;
import com.onyem.khoj.core.domain.MethodToMethodRelationship;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.domain.State;
import com.onyem.khoj.core.repository.ClassRepository;
import com.onyem.khoj.core.repository.MethodRepository;
import com.onyem.khoj.core.repository.PackageRepository;
import com.onyem.khoj.core.service.ClassService;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    PackageRepository packageRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    GraphDatabase graphDatabase;

    @Autowired
    Neo4jTemplate template;

    @Override
    public Clazz addClass(Clazz clazz) {
        Clazz returnClazz = null;
        Transaction tx = graphDatabase.beginTx();
        try {
            if (clazz.getId() == null) {
                Package pkg = clazz.getPkg();
                if (pkg != null) {
                    Package foundPkg = packageRepository.findByName(pkg.getName());
                    if (foundPkg != null) {
                        clazz.setPkg(foundPkg);
                    } else {
                        pkg.setState(State.COMPLETE);
                    }
                }
                if (clazz.getState() == State.TRANSIENT) {
                    clazz.setState(State.COMPLETE);
                }
                for (Method method : clazz.getMethods()) {
                    if (method.getState() == State.TRANSIENT) {
                        method.setState(State.COMPLETE);
                    }
                }
                returnClazz = classRepository.save(clazz);
            } else {
                Clazz prevClazz = classRepository.findOne(clazz.getId());
                if (!prevClazz.getName().equals(clazz.getName())) {
                    throw new IllegalArgumentException("Cannot update class name");
                }
                if (prevClazz.getState() == State.COMPLETE) {
                    throw new IllegalArgumentException("Finalized node");
                }
                if (clazz.getState() == State.TRANSIENT) {
                    throw new IllegalArgumentException("Invalid state:" + clazz.getState());
                }
                prevClazz.setState(clazz.getState());
                // TODO merge packages
                // TODO merge methods
                returnClazz = classRepository.save(prevClazz);
            }
            tx.success();
        } finally {
            tx.close();
        }
        return returnClazz;
    }

    @Override
    public Clazz findByCanonicalName(String name) {
        String[] names = getPackageAndClassName(name);
        String pkgName = names[0];
        String className = names[1];
        if (pkgName == null) {
            throw new UnsupportedOperationException();
        } else {
            Clazz clazz = classRepository.findByName(className, pkgName);
            return clazz;
        }
    }

    @Override
    public Clazz addClassMethod(Clazz clazz, Method method) {
        Transaction tx = graphDatabase.beginTx();
        try {
            clazz = classRepository.findOne(clazz.getId());
            if (clazz == null) {
                throw new IllegalArgumentException("Not persisted: " + clazz);
            }
            if (clazz.getState() == State.COMPLETE) {
                throw new IllegalArgumentException("Finalized node");
            }
            method.setClazz(clazz);
            if (method.getState() == State.TRANSIENT) {
                method.setState(State.COMPLETE);
            }
            methodRepository.save(method);
            tx.success();
            return classRepository.findOne(clazz.getId());
        } finally {
            tx.close();
        }
    }

    @Override
    public boolean addMethodInvokes(Method source, Method destination) {
        Transaction tx = graphDatabase.beginTx();
        boolean created = false;
        try {
            Object o = template.createRelationshipBetween(source, destination, MethodToMethodRelationship.class,
                    "INVOKES", false);
            created = o != null;
            tx.success();
        } finally {
            tx.close();
        }
        return created;
    }

    @Override
    public Set<Method> getMethodsInvoked(Method source) {
        Set<Method> methods = methodRepository.findMethodsByInvoked(source.getId());
        return methods.stream().map(m -> methodRepository.findOne(m.getId())).collect(Collectors.toSet());
    }

    private String[] getPackageAndClassName(String name) {
        int lastDot = name.lastIndexOf(".");
        if (lastDot > 0) {
            String pkg = name.substring(0, lastDot);
            String className = name.substring(lastDot + 1);
            return new String[] { pkg, className };
        } else {
            return new String[] { null, name };
        }
    }

}
