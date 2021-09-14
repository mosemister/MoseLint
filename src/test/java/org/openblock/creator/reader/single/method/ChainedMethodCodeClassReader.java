package org.openblock.creator.reader.single.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.function.caller.FunctionCaller;
import org.openblock.creator.code.line.CallingLine;
import org.openblock.creator.code.line.MultiLine;
import org.openblock.creator.code.line.returning.ReturnLine;
import org.openblock.creator.code.variable.VariableCaller;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.custom.function.method.CustomMethod;
import org.openblock.creator.project.Project;

import java.util.List;
import java.util.Optional;

public class ChainedMethodCodeClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public class EmptyClassReader {",
            "\tpublic void print() {",
            "\t\tSystem.out.println(this.helloWorld());",
            "\t}",
            "",
            "\tpublic String helloWorld() {",
            "\t\treturn this.trim(\"Hello World!\");",
            "\t}",
            "",
            "\tpublic String trim(String param) {",
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
    public void testTrimMethod() {
        Optional<CustomMethod> opFunction = customClass
                .getFunctions(CustomMethod.class)
                .stream()
                .filter(f -> f.getName().equals("trim"))
                .findAny();
        if (opFunction.isEmpty()) {
            Assertions.fail("Could not find trim function");
            return;
        }
        CustomMethod trim = opFunction.get();
        List<Codeable> codeBlock = trim.getCodeBlock();
        Assertions.assertEquals(1, codeBlock.size());

        Codeable block = codeBlock.get(0);
        if (!(block instanceof ReturnLine returnLine)) {
            Assertions.fail("Expected ReturnLine but found: " + block.getClass().getName());
            return;
        }
        Optional<CallingLine> opLine = returnLine.getLine();
        Assertions.assertTrue(opLine.isPresent(), "Return doesnt have a value");

        CallingLine callingLine = opLine.get();
        if (!(callingLine instanceof MultiLine multiLine)) {
            Assertions.fail("Line was not a multi line ");
            return;
        }
        List<CallingLine> lines = multiLine.getLines();
        Assertions.assertEquals(2, lines.size());

        CallingLine param = lines.get(0);
        if (!(param instanceof VariableCaller vCaller)) {
            Assertions.fail("Expected VariableCaller however gained: " + param.getClass().getName());
            return;
        }
        Assertions.assertEquals("param", vCaller.getCallable().getName());

        CallingLine trimFunction = lines.get(1);
        if (!(trimFunction instanceof FunctionCaller<?> fCaller)) {
            Assertions.fail("Expected FunctionCaller however gained: " + trimFunction.getClass().getName());
            return;
        }
        Assertions.assertEquals("trim", fCaller.getCallable().getName());
        Assertions.assertEquals(0, fCaller.getCallable().getParameters().size());
    }

}
