package com.samujjwaal.hw1ProjectFiles.structural;

import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.IOException;

public class Adapter implements DesignPattern {

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger =  LoggerFactory.getLogger(Adapter.class);


    public String[] defaultClasses = {"Target", "Adaptee","Adapter"};
    public String packageName = "com.StructuralDP.adapter";
    JavaFile[] generatedCode = new JavaFile[defaultClasses.length];


    public Adapter(int flag)throws IOException{
        logger.info("Executing Adapter()");
        if(flag == 1) {
            createDesignPattern(defaultClasses, packageName);
        }
    }

    @Override
    public String getDefaultPackageName() {
        return packageName;
    }

    @Override
    public String[] getDefaultClassName() {
        return defaultClasses;
    }

    public JavaFile[] generateCode(String[] classes, String packageName){

        int i = 0;
        logger.info("Executing generateCode()");


//      Target interface declaration
        ClassName Target = ClassName.get("",classes[i]);
        TypeSpec target = TypeSpec.interfaceBuilder(Target)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Target interface, defines domain-specific interface to which Adaptee will be adapted")
                .addMethod(MethodSpec.methodBuilder("request")
                        .addModifiers(Modifier.ABSTRACT,Modifier.PUBLIC)
                        .returns(String.class)
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,target)
                .skipJavaLangImports(true)
                .build();
        i += 1;
//      Adaptee class declaration
        ClassName Adaptee = ClassName.get("",classes[i]);
        TypeSpec adaptee = TypeSpec.classBuilder(Adaptee)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Adaptee class, interface which will be adapted")
                .addMethod(MethodSpec.methodBuilder("specialRequest")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return $S","specialRequest")
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,adaptee)
                .skipJavaLangImports(true)
                .build();
        i += 1;
//      Adapter class declaration
        ClassName Adapter =ClassName.get("",classes[i]);
        TypeSpec adapter = TypeSpec.classBuilder(Adapter)
                .addSuperinterface(Target)
                .addJavadoc("Adapter class, adapts Adaptee to the Target interface")
                .addModifiers(Modifier.PUBLIC)
                .addField(Adaptee, "adaptee",Modifier.PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Adaptee, "adaptee")
                        .addStatement("this.adaptee = adaptee")
                        .build())
                .addMethod(MethodSpec.methodBuilder("request")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return adaptee.specialRequest()")
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,adapter)
                .skipJavaLangImports(true)
                .build();

        logger.info("Returning generated java code to be written in files");

        return generatedCode;

    }
}
