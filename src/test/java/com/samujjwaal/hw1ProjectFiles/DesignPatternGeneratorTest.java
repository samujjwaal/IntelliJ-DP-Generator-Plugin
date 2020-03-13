package com.samujjwaal.hw1ProjectFiles;

import com.samujjwaal.hw1ProjectFiles.structural.Flyweight;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DesignPatternGeneratorTest {

    @Test
    void getInstanceTest() {
        // Test Case to verify only 1 instance of Hw1DesignPatternGenerator is created each time

        DesignPatternGenerator firstInstance = DesignPatternGenerator.getInstance();
        DesignPatternGenerator secondInstance = DesignPatternGenerator.getInstance();

        assertEquals(firstInstance,secondInstance);
    }

    @Test
    void chooseDesignPatternTest() throws IOException {

        //Test Case to verify correct design pattern file is executed when user inputs the choice of design pattern
        // Here Flyweight( 11 ) is selected.

        DesignPattern design_pattern2 = DesignPatternGenerator.getInstance().chooseDesignPattern(11,0);
        assertEquals(Flyweight.class,design_pattern2.getClass());

        // Test Case to check null is returned on selecting incorrect design pattern choice
        int choice = 75;
        DesignPattern design_pattern = DesignPatternGenerator.getInstance().chooseDesignPattern(choice,0);
        assertNull(design_pattern);
    }
}