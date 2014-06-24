package com.onyem.khoj.parser.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.onyem.khoj.core.service.CoreModule;

@Configuration
@Import(CoreModule.class)
@ComponentScan("com.onyem.khoj.parser.service.impl")
public class ParserModule {

}
