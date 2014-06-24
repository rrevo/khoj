package com.onyem.khoj.core.service;

import com.onyem.khoj.core.domain.Clazz;

public interface ClassService {

    Clazz addClass(Clazz clazz);

    Clazz findByCanonicalName(String name);

}
