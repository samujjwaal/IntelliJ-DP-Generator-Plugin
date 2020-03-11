package com.samujjwaal.designpatternplugin;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.squareup.javapoet.JavaFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.Objects;

public class DPDialogWrapper extends DialogWrapper {


    private AnActionEvent event;

    private JPanel panel = new JPanel(new GridBagLayout());
    private JTextField packageName;
    private JTextField[] classNames;

    private DesignPattern designPatternSelected;
    private String[] defaultClassesNames;

    public void setParams(DesignPattern dp,String[] defaultClasses, String defaultPackageName, AnActionEvent e){
        int n = defaultClasses.length;
        event = e;
        packageName = new JTextField(defaultPackageName);
        classNames = new JTextField[n];


        designPatternSelected = dp;
        defaultClassesNames = defaultClasses;

    }

    public DPDialogWrapper(DesignPattern dp,String[] defaultClasses, String defaultPackageName, AnActionEvent e) {
        super(true);  // use the current window as parent
        setParams(dp,defaultClasses,defaultPackageName,e);
        init();
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        packageName.setForeground(JBColor.darkGray);

        for (int i = 0; i < defaultClassesNames.length ; i++ ){
            classNames[i] = new JTextField(defaultClassesNames[i]);
            classNames[i].setForeground(JBColor.gray);

            int finalI = i;
            classNames[i].addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    classNames[finalI].setForeground(JBColor.BLACK);
                    classNames[finalI].setText("");

                }
                @Override
                public void focusLost(FocusEvent e) {
                }
            });
        }
        GridBag gb = new GridBag()
                .setDefaultInsets(JBUI.insets(0, 0, AbstractLayout.DEFAULT_VGAP,
                        AbstractLayout.DEFAULT_HGAP))
                .setDefaultWeightX(1.0)
                .setDefaultFill(GridBagConstraints.HORIZONTAL);

        panel.setSize(new Dimension(450,classNames.length*50));


        panel.add(label("Enter Package Name:"),gb.nextLine().next().weightx(0.1));
        panel.add(packageName,gb.next().weightx(0.2));
        panel.add(label(""),gb.nextLine().next().weightx(0.1));
        panel.add(label("Enter Class Names:"),gb.nextLine().next().weightx(0.1));
        panel.add(label(""),gb.nextLine().next().weighty(0.1));

        for (JTextField className : classNames){
            panel.add(label(String.format("For %s:", className.getText() )),gb.nextLine().next().weightx(0.1));
            panel.add(className,gb.next().weightx(1));
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        String pckName = packageName.getText();
        String[] classname = new String[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            classname[i] = classNames[i].getText();
        }

        dispose();

        JavaFile[] files = designPatternSelected.generateCode(classname,pckName);
        PsiFile[] writeFiles = new PsiFile[files.length];

        WriteCommandAction.runWriteCommandAction(event.getProject(), new Runnable() {
            @Override
            public void run() {
                try {
                    PsiDirectory outputDir = PsiManager.getInstance(Objects.requireNonNull(event.getProject())).findDirectory(event.getProject().getBaseDir().createChildDirectory(null,pckName));
                    for (int i = 0; i < files.length; i++) {
                        writeFiles[i] = PsiFileFactory.getInstance(Objects.requireNonNull(event.getProject())).createFileFromText(String.format("%s.java", classname[i]), JavaLanguage.INSTANCE, files[i].toString());
                        int finalI = i;
                        WriteCommandAction.runWriteCommandAction(event.getProject(), new Runnable() {
                            @Override
                            public void run() {
                                assert outputDir != null;
                                outputDir.add(writeFiles[finalI]);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void doCancelAction() {
        dispose();
    }

    private JComponent label (String text){
        JBLabel label = new JBLabel(text);
        label.setComponentStyle(UIUtil.ComponentStyle.SMALL);
        label.setFontColor(UIUtil.FontColor.BRIGHTER);
        label.setBorder(JBUI.Borders.empty(0,5,2,0));
        return label;
    }
}
