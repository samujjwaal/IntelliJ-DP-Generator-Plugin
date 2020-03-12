package com.samujjwaal.designpatternplugin;

import ch.qos.logback.classic.Logger;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public class GetUserInput extends AnAction {
    //Define a static logger variable so that it references the Logger instance
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GetUserInput.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        logger.info("Entering {} ",GetUserInput.class.getSimpleName());

        logger.info("Executing actionPerformed()");

        logger.info("Creating instance of {}", ChooseDesignPattern.class.getSimpleName());
        ChooseDesignPattern c = new ChooseDesignPattern();
        c.createDropdown(e);

        logger.info("End of execution");
    }
}
