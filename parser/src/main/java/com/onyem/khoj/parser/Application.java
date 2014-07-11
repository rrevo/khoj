package com.onyem.khoj.parser;

import java.io.File;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.onyem.khoj.core.domain.Clazz;
import com.onyem.khoj.core.service.ClassService;
import com.onyem.khoj.parser.service.ClassParserService;
import com.onyem.khoj.parser.service.ParserModule;

@Configuration
@ComponentScan(basePackageClasses = ParserModule.class)
public class Application {

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("neo4j.db");
    }

    @Autowired
    ClassParserService classParserService;

    @Autowired
    ClassService classService;

    public void run() throws Exception {

        String className = "com.onyem.khoj.parser.service.impl.ClassParserServiceImpl";
        ClassReader classReader = new ClassReader(className);
        ClassWriter classWriter = new ClassWriter(0);
        classReader.accept(classWriter, 0);
        byte[] bytes = classWriter.toByteArray();

        Clazz clazz = classParserService.addClass(bytes);
        System.out.println("Class: " + clazz);

        Clazz superClazz = classService.getClassExtends(clazz);
        System.out.println("Super Class: " + superClazz);

        Set<Clazz> interfaces = classService.getClassImplements(clazz);
        for (Clazz interfaceClazz : interfaces) {
            System.out.println("Interface :" + interfaceClazz);
        }
    }

    public static void main(String[] args) throws Exception {
        FileUtils.deleteRecursively(new File("neo4j.db"));

        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(Application.class);
            ctx.refresh();

            Application application = ctx.getBean(Application.class);
            application.run();
        }
    }

}
