package com.samujjwaal.designpatternplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class GetUserInput extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        AskDesignPattern(e);
    }

    private void AskDesignPattern(@NotNull AnActionEvent e) {

        String dpName = Messages.showInputDialog(e.getProject(),"Enter Design Pattern Name",
                "Design Pattern Generator",Messages.getQuestionIcon());

        String test = Messages.showInputDialog(e.getProject(),"Enter some text",
                "Test",Messages.getQuestionIcon());

        Messages.showMessageDialog(e.getProject(), String.format("%s The Chosen Design Pattern Is %s",test,dpName),
                "Design Pattern Generator", Messages.getInformationIcon());




    }
}
