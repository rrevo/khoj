package com.onyem.khoj.core.service.impl;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Service;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.domain.Package;
import com.onyem.khoj.core.repository.ClassRepository;
import com.onyem.khoj.core.repository.PackageRepository;
import com.onyem.khoj.core.service.ClassService;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    ClassRepository classRepository;

    @Autowired
    PackageRepository packageRepository;

    @Autowired
    GraphDatabase graphDatabase;

    @Override
    public Clazz addClass(Clazz clazz) {
        Clazz returnClazz = null;
        Transaction tx = graphDatabase.beginTx();
        try {
            Package pkg = clazz.getPkg();
            if (pkg != null) {
                Package foundPkg = packageRepository.findByName(pkg.getName());
                if (foundPkg != null) {
                    clazz.setPkg(foundPkg);
                }
            }
            returnClazz = classRepository.save(clazz);
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
            // TODO Fix this search to use package name as well
            return classRepository.findByName(className);
        } else {
            Clazz clazz = classRepository.findByName(className);
            return clazz;
        }
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
