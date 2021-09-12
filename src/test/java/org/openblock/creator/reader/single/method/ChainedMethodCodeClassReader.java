package org.openblock.creator.reader.single.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.custom.function.method.CustomMethod;
import org.openblock.creator.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ChainedMethodCodeClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public class EmptyClassReader {",
            "\tpublic void print() {",
            "\t\tSystem.out.println(this.helloWorld())",
            "\t}",
            "",
            "\tpublic String helloWorld() {",
            "\t\treturn this.trim(\"Hello World!\");",
            "\t}",
            "",
            "\tpublic void trim(String param) {",
            "\t\treturn param.trim();",
            "\t}",
            "}"};
    private static final Project<AbstractCustomClass> project = new Project<>("Test");
    private static final CustomClassReader reader = new CustomClassReader(classAsString);

    private static AbstractCustomClass customClass;

    @BeforeAll
    public static void init() {
        customClass = reader.readStageOne(project);
        customClass = reader.readStageTwo(project, customClass);
        customClass = reader.readStageThree(project, customClass);
    }

    @Test
    public void testMethodCount() {
        Assertions.assertEquals(3, customClass.getFunctions(CustomMethod.class).size());
    }

    @Test
    public void testOrderOfMethods() {
        List<CustomMethod> methods = new ArrayList<>(customClass.getFunctions(CustomMethod.class));
        Assertions.assertEquals("", methods.get(0).getName());
        Assertions.assertEquals("", methods.get(1).getName());
        Assertions.assertEquals("", methods.get(2).getName());
    }
}
