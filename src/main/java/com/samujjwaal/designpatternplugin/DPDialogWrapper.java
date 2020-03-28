package com.samujjwaal.designpatternplugin;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class DPDialogWrapper extends DialogWrapper {

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger =  LoggerFactory.getLogger(DPDialogWrapper.class);

    // project instance
    private Project project;

    // file handling class instance
    OutputFileHandler handler;

    // boolean flag to check for class name clash
    boolean[] nameClash;

    // panel
    private JPanel panel = new JPanel(new GridBagLayout());

    // text fields
    private JTextField packageName;
    private JTextField[] classNamesTextField;

    // DesignPattern instance
    private DesignPattern designPatternSelected;
    private String dpName;
    private String[] defaultClassesNames;

    public void setParams(DesignPattern dp,String[] defaultClasses, String defaultPackageName, Project p){
        int n = defaultClasses.length;

        //initializing all fields
        project = p;
        packageName = new JTextField(defaultPackageName);
        classNamesTextField = new JTextField[n];

        designPatternSelected = dp;
        defaultClassesNames = defaultClasses;

    }

    // method to validate each text field when OK button is pressed
    @Nullable
    @Override
    protected ValidationInfo doValidate() {

//      to check if package name is empty
        if(packageName.getText().equals("")){
            return new ValidationInfo("Package Name can't be empty",packageName);
        }

        for (JTextField field : classNamesTextField ){
            String text = field.getText();

//            To check if input is null
            if(text.equals("")){

                return new ValidationInfo("Class Name can't be empty",field);
            }
//          To check if input class name is legal class name
            if(!DesignPattern.validateInput(text)){

                return new ValidationInfo("Invalid Class Name entered",field);

            }
        }
        return null;
    }

    public DPDialogWrapper(DesignPattern dp, String[] defaultClasses, String defaultPackageName, Project p,String designPatternName) {
        super(true);  // use the current window as parent
        setParams(dp,defaultClasses,defaultPackageName,p);
        dpName = designPatternName;
        init();
        setTitle("Input Values for " + dpName + " Pattern");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        packageName.setForeground(JBColor.gray);
        packageName.requestFocus(false);
        packageName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                packageName.setForeground(JBColor.black);
                packageName.setSelectionStart(4);
                packageName.setSelectionEnd(packageName.getText().length());
            }
        });

        //setting default values to text fields
        for (int i = 0; i < defaultClassesNames.length ; i++ ){
            classNamesTextField[i] = new JTextField(defaultClassesNames[i]);
            classNamesTextField[i].setForeground(JBColor.gray);

            // when user interacts with the text field
            int finalI = i;
            classNamesTextField[i].addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {

                    if(classNamesTextField[finalI].getForeground() == JBColor.gray){
                        classNamesTextField[finalI].setForeground(JBColor.BLACK);
                    }
                    classNamesTextField[finalI].selectAll();

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

        setOKButtonText("Finish");
        setOKButtonTooltip("Generate files for " + dpName);
        nameClash = new boolean[classNamesTextField.length];
        Arrays.fill(nameClash, true);
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

        // instantiate handler object
        handler = new OutputFileHandler(project,pckName,inputClassname,designPatternSelected);

        if(isClash()){
            for (int i = 0; i < inputClassname.length; i++) {
//              checking if same name class exists in the directory
                if(handler.checkClassNameClash(inputClassname[i])){
                    classNamesTextField[i].setForeground(JBColor.RED);
                    nameClash[i] =true;
                }
                else{
                    classNamesTextField[i].setForeground(JBColor.black);
                    nameClash[i] = false;
                }
            }
        }

        if(!isClash()){

            logger.info("Writing java code into output files");

            handler.outputPsiFilesToDir();

            // for closing panel
            dispose();
        }
        else{
            // Error message for name clash
            String message = "Possible Class Name Clash detected in the given package name.\n\n" +
                             "Rename the highlighted Class Names or Enter a new Package Name.";
            Messages.showErrorDialog(message,"Class Name Clash Error");
        }
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

    private boolean isClash(){
        for(boolean value: nameClash){
            if(value){ return true;}
        }
        return false;
    }
}