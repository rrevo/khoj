package com.onyem.khoj.parser.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onyem.khoj.core.domain.Artifact;
import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.service.ClassService;
import com.onyem.khoj.parser.service.ClassParserService;
import com.onyem.khoj.parser.service.JarParserService;

@Service
public class JarParserServiceImpl implements JarParserService {

    @Autowired
    ClassParserService classParserService;

    @Autowired
    ClassService classService;

    @Override
    public Artifact addJar(Artifact artifact, File file) {
        Artifact returnArtifact = classService.addArtifact(artifact);

        try (JarFile jarFile = new JarFile(file, false)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    Clazz clazz = classParserService.addClass(bytes);
                    classService.addArtifactContainsClasses(returnArtifact, Collections.singleton(clazz));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return returnArtifact;
    }
}