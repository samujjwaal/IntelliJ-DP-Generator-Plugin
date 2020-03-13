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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.Objects;

public class DPDialogWrapper extends DialogWrapper {

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger =  LoggerFactory.getLogger(DPDialogWrapper.class);

    // event instance
    private AnActionEvent event;

    // panel
    private JPanel panel = new JPanel(new GridBagLayout());

    // text fields
    private JTextField packageName;
    private JTextField[] classNamesTextField;

    // DesignPattern instance
    private DesignPattern designPatternSelected;
    private String[] defaultClassesNames;

    public void setParams(DesignPattern dp,String[] defaultClasses, String defaultPackageName, AnActionEvent e){
        int n = defaultClasses.length;

        //initializing all fields
        event = e;
        packageName = new JTextField(defaultPackageName);
        classNamesTextField = new JTextField[n];

        designPatternSelected = dp;
        defaultClassesNames = defaultClasses;

    }

    public DPDialogWrapper(DesignPattern dp,String[] defaultClasses, String defaultPackageName, AnActionEvent e) {
        super(true);  // use the current window as parent
        setParams(dp,defaultClasses,defaultPackageName,e);
        init();
        setTitle("Input Values");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        packageName.setForeground(JBColor.darkGray);

        //setting default values to text fields
        for (int i = 0; i < defaultClassesNames.length ; i++ ){
            classNamesTextField[i] = new JTextField(defaultClassesNames[i]);
            classNamesTextField[i].setForeground(JBColor.gray);

            // when user interacts with the text field
            int finalI = i;
            classNamesTextField[i].addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    classNamesTextField[finalI].setForeground(JBColor.BLACK);
                    classNamesTextField[finalI].setText("");
                }
                @Override
                public void focusLost(FocusEvent e) {
                }
            });
        }

        // gridbag layout object
        GridBag gb = new GridBag()
                .setDefaultInsets(JBUI.insets(0, 0, AbstractLayout.DEFAULT_VGAP,
                        AbstractLayout.DEFAULT_HGAP))
                .setDefaultWeightX(1.0)
                .setDefaultFill(GridBagConstraints.HORIZONTAL);

//        set panel size
        panel.setSize(new Dimension(450, classNamesTextField.length * 50));

//      add text fields
        panel.add(label("Enter Package Name:"),gb.nextLine().next().weightx(0.1));
        panel.add(packageName,gb.next().weightx(0.2));

        panel.add(label(""),gb.nextLine().next().weightx(0.1));

        panel.add(label("Enter Class Names:"),gb.nextLine().next().weightx(0.1));
        panel.add(label(""),gb.nextLine().next().weighty(0.1));

//        text fields for all class names
        for (JTextField className : classNamesTextField){
            panel.add(label(String.format("For %s:", className.getText() )),gb.nextLine().next().weightx(0.1));
            panel.add(className,gb.next().weightx(1));
        }
        return panel;
    }

    @Override
    protected void doOKAction() {

        // retrieving user input
        String pckName = packageName.getText();
        String[] inputClassname = new String[classNamesTextField.length];
        for (int i = 0; i < classNamesTextField.length; i++) {
            inputClassname[i] = classNamesTextField[i].getText();
        }

        // for closing panel
        dispose();

        // generating JavaFile of each design pattern class
        logger.info("Generating java code using JavaPoet");
        JavaFile[] files = designPatternSelected.generateCode(inputClassname,pckName);

        PsiFile[] writeFiles = new PsiFile[files.length];

        logger.info("Writing java code into output files");
        WriteCommandAction.runWriteCommandAction(event.getProject(), new Runnable() {
            @Override
            public void run() {
                try {
                    // creating output directory
                    PsiDirectory outputDir = PsiManager.getInstance(Objects.requireNonNull(event.getProject())).findDirectory(event.getProject().getBaseDir().createChildDirectory(null,pckName));
                    for (int i = 0; i < files.length; i++) {

                        // create PsiFile of each generated java file
                        writeFiles[i] = PsiFileFactory.getInstance(Objects.requireNonNull(event.getProject())).createFileFromText(String.format("%s.java", inputClassname[i]), JavaLanguage.INSTANCE, files[i].toString());
                        int finalI = i;
                        WriteCommandAction.runWriteCommandAction(event.getProject(), new Runnable() {
                            @Override
                            public void run() {
                                assert outputDir != null;

                                // adding PsiFile to output directory
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
        // custom label properties
        JBLabel label = new JBLabel(text);
        label.setComponentStyle(UIUtil.ComponentStyle.SMALL);
        label.setFontColor(UIUtil.FontColor.BRIGHTER);
        label.setBorder(JBUI.Borders.empty(0,5,2,0));
        return label;
    }
}
