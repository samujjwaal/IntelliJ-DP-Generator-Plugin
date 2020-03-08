package com.samujjwaal.designpatternplugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DPDialogWrapper extends DialogWrapper {

    private JPanel panel = new JPanel(new GridBagLayout());
    private JTextField packageName = new JTextField("com.CreationalDP.prototype") ;
    private JTextField[] classNames;

    private DesignPattern designPatternSelected;
    private String[] defaultClassesNames = {"Prototype","ConcretePrototype","Client"};

    public DPDialogWrapper(DesignPattern dp,String[] defaultClasses, String defaultPackageName, AnActionEvent e) {
        super(true);  // use the current window as parent
        init();
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        for (int i = 0; i < defaultClassesNames.length ; i++ ){
            classNames[i] = new JTextField(defaultClassesNames[i]);
            classNames[i].setForeground(JBColor.gray);
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
        return panel;
    }

    @Override
    protected void doOKAction() {
        dispose();

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
