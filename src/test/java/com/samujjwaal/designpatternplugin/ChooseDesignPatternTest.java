package com.samujjwaal.designpatternplugin;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ChooseDesignPatternTest {

    ChooseDesignPattern test = new ChooseDesignPattern();

    @Test
    void createHashMapTest() {

//      Test Case to verify appropriate hashmap is created for the design patterns

        test.createHashMap(test.designPatterns);

        assertEquals(HashMap.class,test.designPatternsHashMap.getClass());
    }

    @Test
    void getDesignPatternKeyTest() {

//      Test case to check correct design pattern key is returned for a selected design pattern from the hashmap

        String testChoice = "singleton";
        int key = test.getDesignPatternKey(testChoice);

        assertEquals(1,key);

    }
}