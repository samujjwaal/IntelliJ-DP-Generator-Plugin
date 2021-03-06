package com.samujjwaal.hw1ProjectFiles.behavioral;

import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.IOException;

public class Command implements DesignPattern {
    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger = LoggerFactory.getLogger(Command.class);


    public String[] defaultClasses = {"Command", "Invoker","Receiver","ConcreteCommand"};
    public String packageName = "com.BehavioralDP.command";
    JavaFile[] generatedCode = new JavaFile[defaultClasses.length];

    public Command(int flag)throws IOException{
        logger.info("Executing Command()");
        if(flag == 1) {
            createDesignPattern(defaultClasses, packageName);
        }    }

        @Override
        public String getDefaultPackageName() {
            return packageName;
        }
    
        @Override
        public String[] getDefaultClassName() {
            return defaultClasses;
        }

    public JavaFile[] generateCode(String[] classes, String packageName){
        logger.info("Executing generateCode()");

        int i = 0;

//        Command interface declaration
        ClassName Command = ClassName.get("",classes[i]);
        MethodSpec execute = MethodSpec.methodBuilder("execute")
                .addModifiers(Modifier.ABSTRACT,Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .build();
        TypeSpec command = TypeSpec.interfaceBuilder(Command)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Command interface, declares an interface for executing an operation")
                .addMethod(execute)
                .build();
        generatedCode[i] = JavaFile.builder(packageName,command)
                .skipJavaLangImports(true)
                .build();
        i += 1;

//        Invoker class declaration
        ClassName Invoker = ClassName.get("",classes[i]);
        TypeSpec invoker = TypeSpec.classBuilder(Invoker)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Invoker class, asks the command to carry out the request")
                .addField(Command, "command", Modifier.PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Command,"command")
                        .addStatement("this.$N = command", "command")
                        .build())
                .addMethod(MethodSpec.methodBuilder(execute.name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("$N.$N()","command", execute.name)
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,invoker)
                .skipJavaLangImports(true)
                .build();
        i += 1;

//        Receiver class declaration
        ClassName Receiver = ClassName.get("",classes[i]);
        FieldSpec opPerf = FieldSpec.builder(Boolean.TYPE,"operationPerfomed")
                .addModifiers(Modifier.PRIVATE)
                .initializer("false")
                .build();
        TypeSpec receiver = TypeSpec.classBuilder(Receiver)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Receiver class, knows how to perform the operations associated with carrying\n" +
                            "out a request")
                .addField(opPerf)
                .addMethod(MethodSpec.methodBuilder("action")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("$N = true",opPerf.name)
                        .build())
                .addMethod(MethodSpec.methodBuilder("isOperationPerfomed")
                        .addModifiers(Modifier.PROTECTED)
                        .returns(TypeName.BOOLEAN)
                        .addStatement("return $N",opPerf.name)
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,receiver)
                .skipJavaLangImports(true)
                .build();
        i += 1;

//        ConcreteCommand class declaration
        ClassName ConcreteCommand = ClassName.get("",classes[i]);
        TypeSpec concreteCommand = TypeSpec.classBuilder(ConcreteCommand)
                .addSuperinterface(Command)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("ConcreteCommand class, defines a binding between a Receiver object and an\n" +
                            "operation")
                .addField(Receiver, "receiver",Modifier.PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Receiver, "receiver")
                        .addStatement("this.$N = receiver","receiver")
                        .build())
                .addMethod(MethodSpec.methodBuilder(execute.name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("this.$N.$N()","receiver","action")
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,concreteCommand)
                .skipJavaLangImports(true)
                .build();

        logger.info("Returning generated java code to be written in files");

        return generatedCode;
    }
}
