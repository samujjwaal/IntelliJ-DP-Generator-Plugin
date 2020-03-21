## Object-oriented design and implementation of an IntelliJ plugin for a Design Pattern Code Generator.

This project is the implementation of a program to create an IntelliJ IDEA plugin  that generates the template code for a user chosen design pattern and adds it to the existing Java project opened and loaded in IntelliJ IDE. The Design Patterns covered are the 23 Gang of Four(GoF) design patterns, as presented in the book “Design Patterns: Elements of Reusable Object-Oriented Software”. 

------

## Index

1. About Design Patterns
2. About JavaPoet
3. Application Design
4. Test Cases
5. Instructions to Execute
6. Results of Execution

------

#### 1. About Design Patterns

Design patterns are the best practices that a programmer can employ to solve trivial problems while designing an application. Design patterns help in speeding up the development process as they provide a set of time tested, proven development paradigms. They provide an industry standard approach to solve a recurring problem. Using design patterns promotes reusability that leads to more robust and highly maintainable code.

The 23 Design Patterns are classified into 3 main categories:

* Creational Design Patterns
  * Singleton
  * Abstract  Factory
  * Builder
  * Factory Method

* Structural Design Patterns
  * Adapter
  * Bridge
  * Composite
  * Decorator
  * Facade
  * Flyweight
  * Proxy
* Behavioral Design Patterns
  * Chain of Responsibility
  * Command
  * Interpreter
  * Iterator
  * Mediator
  * Memento
  * Observer
  * State
  * Strategy
  * Template
  * Visitor

------

#### 2. About JavaPoet

[JavaPoet](https://github.com/square/javapoet), a successor to [JavaWriter](https://github.com/square/javapoet/tree/javawriter_2), is a Java API for generating .java source files. It can generate primitive types, reference types (like classes, interfaces, enumerated types, anonymous inner classes), fields, methods, parameters, annotations, and Javadocs. 

JavaPoet manages the import of the dependent classes automatically. It uses the ***Builder*** design pattern to specify the logic to generate Java code.

A HelloWorld program like the one here:

```java
package com.example.helloworld;

public final class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello, JavaPoet!");
  }
}
```

can be generated using JavaPoet as follows:

```java
MethodSpec main = MethodSpec.methodBuilder("main")
    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
    .returns(void.class)
    .addParameter(String[].class, "args")
    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
    .build();

TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
    .addMethod(main)
    .build();

JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
    .build();

javaFile.writeTo(System.out);
```

`MethodSpec` "main" is configured with modifiers, return type, parameters and code statements to declare the main method. The main method is added to a `HelloWorld` class and then the class is added to a `HelloWorld.java` file.

JavaPoet offers models for classes & interfaces (`TypeSpec`), fields (`FieldSpec`), methods & constructors (`MethodSpec`), parameters (`ParameterSpec`) and annotations (`AnnotationSpec`). It also generates the package folder structure for the output java source files.

The most recent version of JavaPoet available as of now is 1.12.1.

------

#### 3. Application Design

To implement Design Pattern Code Generator(DePaCoG), I have employed the **Factory Method**, **Template Method** and **Singleton** design patterns.

------

The **Factory Method**, a creational design pattern, creates two basic abstractions: the Product and Creator classes. Both these classes are abstract and clients need to extend them to realize their specific implementations. This pattern is used when there is a super class with multiple sub-classes and based on user input a particular sub-class needs to be instantiated. The responsibility of instantiation of a class is taken from the user to the factory class. 

In the case of DePaCoG, the `DesignPattern` and `DesignPatternGenerator` are the two abstractions I have chosen.

* `DesignPattern` is an `interface` which declares methods for accepting and displaying the class names & package names from the user, generating java source code of the design patterns using JavaPoet and writing the java code into java files as output.

```java
public interface DesignPattern {
    
    void displayClassNames(String[] classes);
    
    String[] setClassNames(String[] oldClasses);
    
    String setPackageName(String defaultPckgName);
    
    void createDesignPattern(String[] oldClasses, String oldPackageName);
    
    JavaFile[] generateCode(String[] classes, String packageName);
    
    void writeJavaFiles(JavaFile[] files);
}
```

There are 23 separate concrete classes(java files) to generate the java source code for each design pattern. Each concrete class `implements DesignPattern`. Interface `DesignPattern` provides the `default` method definition of all methods except the method `generateCode()` as the implementation of all other methods do not change  according to the design patterns. Each concrete class of `DesignPattern` overrides `generateCode()`. 

* `DesignPatternGenerator` is an `abstract class` which declares methods to display a menu of all available design patterns and invoking appropriate classes to generate a  user selected design pattern.

```java
abstract class DesignPatternGenerator {

    protected  String[] designPatterns;

    abstract void displayDesignPatterns();

    abstract void generateDesignPattern();

    abstract void designPatternFactory();
}
```

The field `designPatterns` declares all the design patterns available for creation. All the methods are abstract and are provided with the implementation in the concrete class `Hw1DesignPatternGenerator` which `extends DesignPatternGenerator.` The class `Hw1DesignPatternGenerator` also defines an additional method `chooseDesignPattern(int choice)` which takes in user’s choice of design pattern as input and instantiates an anonymous object of the appropriate design pattern class.

* Advantages of using Factory Method pattern:
  * It provides an approach to “program to an interface, not an implementation”.
  * Through inheritance it provides an abstraction between implementation and client classes. It removes the instantiation of objects of implementation classes from the user/client code
  * It makes the code more robust, easy to extend and less coupled
  * New types of concrete products can be introduced without any modifications to the existing client code
* Disadvantages of using Factory Method pattern:
  * For just creating a single instance of a particular concrete object the user might have to sub-class the creator, which complicates the hierarchy of creator classes
  * Makes the code difficult to interpret as all code is behind an abstraction that may also be hiding another set of abstractions

------

The **Template Method**, a behavioral design pattern, is used to create method stub and defer the steps of implementation to the subclasses. 

The algorithm for generating design patterns has the following steps: display list of all design patterns, ask user which design pattern code is to be generated, ask for and set custom (or default) class names & package name, generate source file using JavaPoet and finally write into output files. The order of execution of these steps cannot be altered as the sequence is critical for the execution of the program. So here different methods are used to achieve the goal of generating the design pattern code. Steps like asking class names& package name and writing generated code into file are common for any design pattern whether it is a creational or structural design pattern. 

This is where Template Method design pattern is useful. It helps define the basic steps to execute an algorithm and can provide default implementation of the methods that might be common for all or a majority of the sub-classes implementing the design pattern. The method is which defines an exclusive property of a design pattern can be overridden by the sub-class.

Template Method is implemented in the `interface DesignPattern` and its sub-classes. Interface `DesignPattern` provides the `default` method definition of all methods except the method `generateCode()` as the implementation of all other methods do not change  according to the design patterns. Each concrete class of `DesignPattern` overrides `generateCode()`.

Inside the `interface DesignPattern`:

```java
JavaFile[] generateCode(String[] classes, String packageName);
```

Inside `class Singleton` which implements `DesignPattern`:

```java
public JavaFile[] generateCode(String[] classes, String packageName){
        int i = 0;
        ClassName Singleton = ClassName.get("", classes[i]);
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        FieldSpec instance = FieldSpec.builder(Singleton, "INSTANCE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .build();
        MethodSpec getInstance = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Singleton)
                .beginControlFlow("if ($N == null)", instance.name)
                .addStatement("$N = new $T()",instance.name, Singleton)
                .endControlFlow()
                .addStatement("return $N", instance.name)
                .build();
        TypeSpec singleton = TypeSpec.classBuilder(Singleton)
                .addModifiers(Modifier.PUBLIC)
                .addField(instance)
                .addMethod(constructor)
                .addMethod(getInstance)
                .build();
        generatedCode[i] = JavaFile.builder(packageName, singleton)
                .skipJavaLangImports(true)
                .build();
        return generatedCode;
    }
```

* Advantages of using Template Method pattern:
  * All the duplicate code/methods of sub-classes can be put together in the super-class
  * Client can override only certain methods of a large algorithm, so that the other components of the algorithm are not affected  
* Disadvantages of using Template Method pattern:
  * Sometimes client might have to change certain fundamental aspects of the algorithm and changing only certain fixed components of the algorithm might not suffice
  * If the number of components in the algorithm being modelled is large, it is harder to maintain the code

------

The **Singleton**, a creational design pattern, that helps in ensuring that a class has only one instance in a program while also providing a global access point to the instance. I have used Singleton to instantiate only one instance of the class `Hw1DesignPatternGenerator` so that in the client class `DePaCoG` the design pattern generating method is only called once at a time. The `instance` field in `Hw1DesignPatternGenerator` is lazy initialized.

Inside class `Hw1DesignPatternGenerator`:

```java
private static Hw1DesignPatternGenerator instance;

private Hw1DesignPatternGenerator(){
    logger.info("Executing constructor Hw1DesignPatternGenerator()");
    generateDesignPattern();
}

public static Hw1DesignPatternGenerator getInstance(){
        if (instance == null) {
            instance = new Hw1DesignPatternGenerator();
        }
        return instance;
    }
```

Creating instance inside `DePaCoG` class:

```java
public class DePaCoG {
    public static void main(String[] args){
        
        Hw1DesignPatternGenerator.getInstance();
    }
}
```

* Advantages of using Singleton pattern:
  * It makes sure that a single instance is instantiated for a class
  * Helps to get a global access point to the instance
  * The object is only initialized when initially requested    
* Disadvantages of using Singleton pattern:
  * Sometimes singleton pattern can hide bad code design
  * It requires modifications to be used in a multi threaded environment to make sure multiple instances are not created by multiple threads

------

Actions of the plugin are added in the `plugin.xml` file as shown below:

```xml
<actions>
    <!-- Add your actions here -->

    <group id="DesignPattern.Generator.MainMenu" text="Design Pattern Generator"
           description="Design pattern generator">
        <add-to-group group-id="MainMenu" anchor="last"/>
        <action class="com.samujjwaal.designpatternplugin.GetUserInput" id="Get.User.Input"
                text="Choose Design Pattern" />
        <separator/>
        <action class="com.samujjwaal.designpatternplugin.OpenGitHub" id="GitHub.Actions.OpenProject"
                text="Open GitHub Project"/>
    </group>

</actions>
```

The class `GetUserInput` is used to initiate the plugin execution and `extends AnAction`. It overrides the method `actionPerformed`, which is invoked whenever the user performs the  associated plugin action.

The class `ChooseDesignPattern` is used to create a `JFrame` with a `ComboBox` element(dropdown menu) to select a design pattern. The class `extends JFrame` and `implements ItemListener`.

Once the design pattern is selected, the respective design pattern generating class from `hw1ProjectFiles` is instantiated and a `DialogWrapper` class is also instantiated.

The class `DPDialogWrapper` is used to create a dialog window for the user to input appropriate class names and package names for the output design pattern files. It `extends DialogWrapper` class. 

A `PsiFile` is created for each generated output class file using `PsiFileFactory` and `PsiDirectory` is used to set the directory of the output files.  Each `PsiFile` is written into the root directory using `runWriteCommandAction()` method of `WriteCommandAction` class.

------

#### 4. Test Cases

There are 3 test classes `ChooseDesignPatternTest`, `DesignPatternTest` and `Hw1DesignPatternGeneratorTest`, with a total of 7 test cases.

* Test Case to verify appropriate hashmap is created for the design patterns  
* Test case to check correct design pattern key is returned for a selected design pattern from the hashmap
* Test Case to verify only 1 instance of `Hw1DesignPatternGenerator` is created each time.
* Test Case to verify correct design pattern file is executed when user inputs the choice of design pattern.
* Test Case to check null is returned on selecting incorrect design pattern choice.
* Test Case to verify  `generateCode()` returns `JavaFile[]` object
* Test Case to verify the generated java files are written successfully in the output folder.

------

#### 5. Instructions to Execute

First clone the repository from Bitbucket.

Open and load the project as an IntelliJ IDEA project and press `Ctrl` twice to open the Run Anything window. Perform the tasks `gradle clean test` and `gradle clean build`. To execute the project, open Gradle tool window and select `Tasks` -> `intellij` -> `runIde` or perform `gradle runIde` in the Run Anything window.

Wait for a new instance of IntelliJ IDEA Community to open and create a new or open an existing Java project to test the plugin. 

The plugin can be used via the Menu option `Design Pattern Generator`, located on the right end of the Main Menu bar.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/MainMenu.png)

Select `Design Pattern Generator` -> `Choose Design Pattern`.A new window is created within the IDE with a dropdown option to select the output design pattern.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/Dropdown1.png)

By default Singleton design pattern is selected and will remain selected even when user presses `Proceed` without making a new selection from the dropdown.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/Dropdown2.png)

After selecting a design pattern and clicking on `Proceed`, a new window is created to get user input for package name and class names of the output class files.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/Dialog1.png)

Default values have been provided in the text fields which can be overwritten by user input. If a text field is left blank(without any text) it will cause an exception. 

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/Dialog2.png)

After user is satisfied with the input values, press `OK` and the design pattern template files are created in the root directory of the project, under a folder with name as the package name. 

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/OutputDir.png)

A `default.conf` configuration file is present at `src/main/resources`. 

If user selects `Design Pattern Generator` -> `Open GitHub Project`, then the GitHub repository of the Design Pattern Code Generator from HW1 is opened in the browser.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/github.png)

------

#### 6. Results of Execution

Here is an example of config values to create Abstract Factory design pattern.

```
{
      designPatternChoice = "2"       //for Abstract Factory Method design pattern
      classes = ["Processor", "Intel", "AMD",
      "OperatingSystem", "ChromeOS", "Ubuntu",
      "LaptopFactory", "ChromeBookFactory","LinuxLaptopFactory"]
      packageName = "com.laptopAbstractFactory"
}
```

For above config input, the output files will be saved in the folder `com.laptopAbstractFactory` in the root directory of the loaded IntelliJ project.

The classes created are : `Processor.java, Intel.java, AMD.java, OperatingSystem.java, OS.java, ChromeOS.java, Ubuntu.java, LaptopFactory.java, ChromeBookFactory.java,LinuxLaptopFactory.java`

The image below shows the class diagram of the classes generated after executing the input values.

![](https://bitbucket.org/samujjwaal/homework2/raw/master/screenshots/hw2/ClassDiagram.png)