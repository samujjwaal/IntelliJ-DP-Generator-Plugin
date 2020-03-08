package com.samujjwaal.hw1ProjectFiles.creational;

import ch.qos.logback.classic.Logger;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.*;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.IOException;

public class Singleton implements DesignPattern {

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Singleton.class);

    public String[] defaultClasses = {"Singleton"};
    public String packageName = "com.CreationalDP.singleton";
    JavaFile[] generatedCode = new JavaFile[defaultClasses.length];

    public Singleton(int flag)throws IOException{
        logger.info("Executing Singleton()");
        if (flag == 1){
            createDesignPattern(defaultClasses, packageName);
        }
//        else{
//            System.out.println("Exit");
//        }
    }

    @Override
    public String getDefaultPackageName() {
        return packageName;
    }

    @Override
    public String[] getDefaultClassName() {
        return defaultClasses;
    }

    @Override
    public JavaFile[] generateCode(String[] classes, String packageName){
        logger.info("Executing generateCode()");
        int i = 0;
        ClassName Singleton = ClassName.get("", classes[i]);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec instance = FieldSpec.builder(Singleton, "INSTANCE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .build();

        MethodSpec getInstance = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Singleton)
                .beginControlFlow("if ($N == null)", instance.name)
                .addStatement("$N = new $T()",instance.name, Singleton)
                .endControlFlow()
                .addStatement("return $N", instance.name)
                .build();

        TypeSpec singleton = TypeSpec.classBuilder(Singleton)
                .addModifiers(Modifier.PUBLIC)
                .addField(instance)
                .addMethod(constructor)
                .addMethod(getInstance)
                .build();

        generatedCode[i] = JavaFile.builder(packageName, singleton)
                .skipJavaLangImports(true)
                .build();
        i += 1;

        logger.info("Returning generated java code to be written in files");

//        javaFile.writeTo(new File("C:\\Users\\Samujjwaal Dey\\Desktop\\CS 474 OOLE\\outputs/singleton.java"));
        return generatedCode;
    }
}
