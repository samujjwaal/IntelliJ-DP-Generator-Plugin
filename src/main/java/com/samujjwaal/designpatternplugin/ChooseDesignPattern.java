package com.samujjwaal.designpatternplugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.samujjwaal.hw1ProjectFiles.DesignPattern;
import com.samujjwaal.hw1ProjectFiles.DesignPatternGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ChooseDesignPattern extends JFrame implements ItemListener{

    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger = LoggerFactory.getLogger(ChooseDesignPattern.class);

    // frame
    static JFrame f;

    // label
    static JLabel l, l1;

    // combobox
    static ComboBox<String> dropdown;

    // panel
    static JPanel p;

    // Buttons for the frame
    static JButton proceedButton;
    static JButton cancelButton;

    // array of string containing design patterns
    String[] designPatterns = {"Singleton","Abstract Factory","Builder","Factory Method","Prototype",
            "Adapter","Bridge","Composite","Decorator","Facade","Flyweight","Proxy",
            "Chain of Responsibility","Command","Interpreter","Iterator","Mediator","Memento","Observer","State","Strategy","Visitor","Template Method"};

//    declare HashMap object
    protected HashMap<Integer, String> designPatternsHashMap = new HashMap<Integer, String>();

    protected void createHashMap(String[] patternList){
        logger.info("Creating hashmap of design patterns");

        HashMap<Integer, String> dpHashMap = new HashMap<Integer, String>();
        for(int i =0; i<patternList.length; i++){

            //removing spaces and converting to lower case
            String value = patternList[i].replaceAll("\\s+","").toLowerCase();
            designPatternsHashMap.put(i + 1, value );
        }
    }

    protected int getDesignPatternKey(String value){
        logger.info("Retrieve design pattern choice from hashmap ");

        // By default, returns singleton as choice
        int key = 1;
        for(Map.Entry entry: designPatternsHashMap.entrySet()){
            String a = (String) entry.getValue();
            if(value.equals(a)){
                key = (int)entry.getKey();
                break; //breaking because its one to one map
            }
        }
        return key;
    }

//    String choice = designPatterns[0].replaceAll("\\s+","").toLowerCase() ;

    public void createDropdown(AnActionEvent event){

        // create a new frame
        f = new JFrame("Choose Design Pattern");

        // create a object
        ChooseDesignPattern dp = new ChooseDesignPattern();

//        Config paramsConfig = ConfigFactory.parseFile(new File("C:\\Users\\Samujjwaal Dey\\Desktop\\CS 474 OOLE\\Homeworks\\homework2\\src\\main\\resources\\default.conf"));
//        Config[] iterations = paramsConfig.getConfigList("iterations").toArray(new Config[0]);

        // set layout of frame
        f.setLayout(new FlowLayout());

        // create dropdown
        dropdown = new ComboBox<>(designPatterns);

        dropdown.addItemListener(dp);

        // create hash map of design patterns
        createHashMap(designPatterns);

        // create labels
        l = new JLabel("Select the Design Pattern ");
        l1 = new JLabel(String.format("%s selected by default", designPatterns[0]));
        l1.setForeground(JBColor.GRAY);

        // create a new panel
        p = new JPanel();
        p.setPreferredSize(new Dimension(300,125));

        p.add(l);

        // add combobox to panel
        p.add(dropdown);

        p.add(l1);

        // add the panel to frame
        f.add(p);

        proceedButton = new JButton("Proceed");

        // action for Proceed button
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();

                logger.info("Creating instance of {}", DesignPatternGenerator.class.getSimpleName());
                DesignPatternGenerator hw2 = DesignPatternGenerator.getInstance();
                try {
                    // Get choice from dropdown
                    String choice = Objects.requireNonNull(dropdown.getSelectedItem()).toString().replaceAll("\\s+","").toLowerCase();
                    int designPatternChoice = getDesignPatternKey(choice);

                    DesignPattern outputDesignPattern = hw2.chooseDesignPattern(designPatternChoice,0);

                    String[] classNames = outputDesignPattern.getDefaultClassName();
                    String packageName = outputDesignPattern.getDefaultPackageName();

                    logger.info("Creating object of {}", DPDialogWrapper.class.getSimpleName());
                    DPDialogWrapper dpWrapper = new DPDialogWrapper(outputDesignPattern,classNames,packageName,event);

                    if(dpWrapper.showAndGet()){
                        dpWrapper.doOKAction();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });

        f.add(proceedButton);
        f.add(cancelButton);

        f.setSize(new Dimension(500,130));

        // center the frame
        f.setLocationRelativeTo(null);

        //To display the frame
        f.setVisible(true);

        //Action on closing frame
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // if the state combobox is changed
        if (e.getSource() == dropdown) {
            String choice;
            choice = Objects.requireNonNull(dropdown.getSelectedItem()).toString();
            logger.info("{} design pattern chosen",choice);
            l1.setText(choice + " selected");
            l1.setForeground(JBColor.black);
        }
    }

}
