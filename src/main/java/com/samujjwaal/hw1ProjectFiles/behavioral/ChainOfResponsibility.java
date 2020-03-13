package com.samujjwaal.hw1ProjectFiles.behavioral;

import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class ChainOfResponsibility implements DesignPattern {

    private static final Logger logger = LoggerFactory.getLogger(ChainOfResponsibility.class);

    public String[] defaultClasses = {"Handler", "ConcreteHandler1","ConcreteHandler2"};
    public String packageName = "com.BehavioralDP.chainOfResponsibility";
    JavaFile[] generatedCode = new JavaFile[defaultClasses.length];

    public ChainOfResponsibility(int flag)throws IOException{
        logger.info("Executing ChainOfResponsibility()");
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
        logger.info("Executing generateCode()");
        int i = 0;

//        Handler class declaration
        ClassName Handler =  ClassName.get("",classes[i]);
        MethodSpec handleReq = MethodSpec.methodBuilder("handleRequest")
                .addModifiers(Modifier.ABSTRACT)
                .returns(TypeName.VOID)
                .build();
        TypeSpec handler = TypeSpec.classBuilder(Handler)
                .addModifiers(Modifier.ABSTRACT)
                .addJavadoc("Handler interface, declares an interface for request handling")
                .addField(Handler,"successor",Modifier.PROTECTED)
                .addMethod(handleReq)
                .addMethod(MethodSpec.methodBuilder("setSuccessor")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addParameter(Handler,"successor")
                        .addStatement("this.$N = $N","successor","successor")
                        .build())
                .build();
        generatedCode[i] = JavaFile.builder(packageName,handler)
                .skipJavaLangImports(true)
                .build();
        i += 1;

//        ConcreteHandler1 class declaration
        ClassName ConcreteHandler1 = ClassName.get("",classes[i]);
        FieldSpec handleReqInv = FieldSpec.builder(Boolean.TYPE,"handleRequestInvoked")
                .addModifiers(Modifier.PRIVATE)
                .initializer("false")
                .build();
        MethodSpec isHandleReqInv = MethodSpec.methodBuilder("isHandleRequestInvoked")
                .addModifiers(Modifier.PROTECTED)
                .returns(Boolean.TYPE)
                .addStatement("return $N",handleReqInv.name)
                .build();
        TypeSpec concHandler1 = TypeSpec.classBuilder(ConcreteHandler1)
                .superclass(Handler)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("ConcreteHandler1 class, handles the request, can access to the next object in " +
                        "a chain and forward the request if necessary.")
                .addField(handleReqInv)
                .addMethod(MethodSpec.methodBuilder(handleReq.name)
                        .returns(TypeName.VOID)
                        .addStatement("$N = true",handleReqInv.name)
                        .addCode("\n")
                        .addComment("if some condition call handleRequest on successor")
                        .beginControlFlow("if ($N)",handleReqInv.name)
                        .addStatement("$N.$N()","successor",handleReq.name)
                        .endControlFlow()
                        .build())
                .addMethod(isHandleReqInv)
                .build();
        generatedCode[i] = JavaFile.builder(packageName,concHandler1)
                .skipJavaLangImports(true)
                .build();
        i += 1;

//        ConcreteHandler2 class declaration
        ClassName ConcreteHandler2 = ClassName.get("",classes[i]);
        TypeSpec concHandler2 = TypeSpec.classBuilder(ConcreteHandler2)
                .superclass(Handler)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("ConcreteHandler2 class, handles the request, can access to the " +
                        "next object in a chain" +
                        " and forward the request if necessary.")
                .addField(handleReqInv)
                .addMethod(MethodSpec.methodBuilder(handleReq.name)
                        .returns(TypeName.VOID)
                        .addStatement("$N = true",handleReqInv.name)
                        .build())
                .addMethod(isHandleReqInv)
                .build();
        generatedCode[i] = JavaFile.builder(packageName,concHandler2)
                .skipJavaLangImports(true)
                .build();

        logger.info("Returning generated java code to be written in files");

        return generatedCode;

    }
}
