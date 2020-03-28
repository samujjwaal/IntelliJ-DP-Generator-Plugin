package com.samujjwaal.designpatternplugin;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OutputFileHandler {

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger =  LoggerFactory.getLogger(OutputFileHandler.class);


    private static Project project;

    private DesignPattern pattern;

    private String packageName;

    private PsiDirectory rootDir;

    private PsiDirectory outputDir;
    
    private String[] classNames;

    private JavaFile[] outputJavaFiles;

    private PsiFile[] outputPsiFiles;

    public OutputFileHandler(Project p, String pckName, String[] cNames, DesignPattern dp) {
        project = p;
        packageName = pckName;
        classNames = cNames;
        pattern = dp;


        // getting root directory
        rootDir = PsiManager.getInstance(project).findDirectory(Objects.requireNonNull(ProjectUtil.guessProjectDir(project)));

        // creating output directory
        createOutputPackageDir(packageName,rootDir);

    }

    protected void outputPsiFilesToDir(){

        // generating JavaFile of each design pattern class
        logger.info("Generating java code using JavaPoet");
        outputJavaFiles = pattern.generateCode(classNames,packageName);
        outputPsiFiles = new PsiFile[outputJavaFiles.length];

        // to write psi files to directory
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < outputJavaFiles.length; i++) {

                    String fileName = classNames[i] + ".java";

                    // create PsiFile of each generated java file
                    outputPsiFiles[i] = PsiFileFactory.getInstance(Objects.requireNonNull(project)).createFileFromText(fileName, JavaLanguage.INSTANCE, outputJavaFiles[i].toString());

                    int finalI = i;
                    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                        @Override
                        public void run() {

                            // adding PsiFile to output directory
                                outputDir.add(outputPsiFiles[finalI]);
                        }
                    });

                    // to open each generated outputJavaFiles in the editor window
                    new OpenFileDescriptor(project, Objects.requireNonNull(outputDir.findFile(fileName))
                                .getVirtualFile()).navigate(true);

                }
            }
        });
    }

    // to check class name clashes
    protected boolean checkClassNameClash(String inputClassName){
        PsiFile[] dirFiles = outputDir.getFiles();
        for (PsiFile file : dirFiles) {
            if(file.getName().equals(inputClassName + ".java")){
                return true;
            }
        }
        return false;
    }

    // to create the output directory
    private void createOutputPackageDir(String pckName, PsiDirectory directory) {

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                int dotCount = StringUtils.countMatches(pckName,".");
                String temp = pckName;

                outputDir = directory ;


                for (int i = 0; i < dotCount; i++) {
                    String subDirName = temp.substring(0,temp.indexOf('.'));

                    PsiDirectory checkDir = outputDir.findSubdirectory(subDirName);

                    if(checkDir != null){
                        outputDir = checkDir;
                    }
                    else {
                        outputDir = outputDir.createSubdirectory(subDirName);
                    }
                    temp = temp.substring(temp.indexOf('.')+1);
                }

                PsiDirectory checkDir = outputDir.findSubdirectory(temp);
                if(checkDir != null){
                    outputDir = checkDir;
                }
                else {
                    outputDir = outputDir.createSubdirectory(temp);
                }
            }
        });
    }
}
