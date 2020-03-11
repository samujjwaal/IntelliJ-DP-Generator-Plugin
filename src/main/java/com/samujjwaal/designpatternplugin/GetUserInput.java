package com.samujjwaal.designpatternplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GetUserInput extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        DPDialogWrapper dpWrapper = new DPDialogWrapper();
//        if(dpWrapper.showAndGet()){
//            dpWrapper.doOKAction();
//        }
//        AskDesignPattern(e);
        ChooseDesignPattern c = new ChooseDesignPattern();
        c.createDropdown(e);
    }

    private void AskDesignPattern(@NotNull AnActionEvent e) {
        String[] designPatterns = {"Singleton","Abstract Factory","Builder","Factory Method","Prototype",
                "Adapter","Bridge","Composite","Decorator","Facade","Flyweight","Proxy",
                "Chain of Responsibility","Command","Interpreter","Iterator","Mediator","Memento","Observer","State","Strategy","Visitor","Template Method"};

        ComboBox<String> dropdown = new ComboBox<>(designPatterns);
        JPanel panel = new JPanel();
        panel.add(dropdown);

        String dpName = Messages.showInputDialog(e.getProject(),"Enter Design Pattern Name",
                "Design Pattern Generator",Messages.getQuestionIcon());

        String test = Messages.showInputDialog(e.getProject(),"Enter some text",
                "Test",Messages.getQuestionIcon());

        Messages.showMessageDialog(e.getProject(), String.format("%s The Chosen Design Pattern Is %s",test,dpName),
                "Design Pattern Generator", Messages.getInformationIcon());
    }
}
