package com.onyem.khoj.parser.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onyem.khoj.parser.service.ClassParserService;
import com.onyem.khoj.parser.service.JarParserService;

@Service
public class JarParserServiceImpl implements JarParserService {

    @Autowired
    ClassParserService classParserService;

    @Override
    public void addJar(File file) {
        try (JarFile jarFile = new JarFile(file, false)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    classParserService.addClass(bytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}