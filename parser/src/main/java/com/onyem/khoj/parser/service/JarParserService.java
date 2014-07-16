package com.onyem.khoj.parser.service;

import java.io.File;

import com.onyem.khoj.core.domain.Artifact;

public interface JarParserService {

    Artifact addJar(Artifact artifact, File file);

}
