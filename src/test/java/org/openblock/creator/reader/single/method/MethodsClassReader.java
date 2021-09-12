package org.openblock.creator.reader.single.method;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.Nameable;
import org.openblock.creator.code.Visibility;
import org.openblock.creator.code.call.Returnable;
import org.openblock.creator.code.call.returntype.ReturnType;
import org.openblock.creator.code.call.returntype.StatedReturnType;
import org.openblock.creator.code.clazz.ClassType;
import org.openblock.creator.code.clazz.IClass;
import org.openblock.creator.code.clazz.type.BasicType;
import org.openblock.creator.code.clazz.type.IType;
import org.openblock.creator.code.clazz.type.VoidType;
import org.openblock.creator.code.function.IFunction;
import org.openblock.creator.code.line.primitive.StringConstructor;
import org.openblock.creator.code.line.returning.ReturnLine;
import org.openblock.creator.code.variable.parameter.Parameter;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.custom.clazz.standardtype.CustomStandardClass;
import org.openblock.creator.impl.java.clazz.JavaClass;
import org.openblock.creator.project.Project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MethodsClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public class EmptyClassReader {",
            "\tpublic void testMethod() {",
            "\t}",
            "",
            "\tpublic String testHelloWorld() {",
            "\t\treturn \"Hello World!\";",
            "\t}",
            "",
            "\tpublic void parameterTest(String param) {",
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
    public void testReadClassType() {
        Assertions.assertNotNull(customClass.getClassType());
        Assertions.assertEquals(ClassType.STANDARD, customClass.getClassType());
    }

    @Test
    public void testReadClass() {
        Assertions.assertTrue(customClass instanceof CustomStandardClass);
    }

    @Test
    public void testReadVisibility() {
        Assertions.assertNotNull(customClass.getVisibility());
        Assertions.assertEquals(Visibility.PUBLIC, customClass.getVisibility());
    }

    @Test
    public void testReadPackage() {
        Assertions.assertNotNull(customClass.getPackage());
        Assertions.assertArrayEquals(
                new String[]{"org", "openblock", "creator", "reader"},
                customClass.getPackage());
    }

    @Test
    public void testReadName() {
        Assertions.assertNotNull(customClass.getName());
        Assertions.assertEquals("EmptyClassReader", customClass.getName());
    }

    @Test
    public void testReadNoAbstract() {
        Assertions.assertFalse(customClass.isAbstract());
    }

    @Test
    public void testReadNoFinal() {
        Assertions.assertFalse(customClass.isFinal());
    }

    @Test
    public void testReadNoExtends() {
        if (!(customClass instanceof CustomStandardClass standard)) {
            Assertions.fail("custom class is not CustomStandardClass");
            return;
        }
        Assertions.assertTrue(standard.getExtendingClass().isEmpty());
    }

    @Test
    public void testReadEmptyGenerics() {
        Assertions.assertTrue(customClass.getGenerics().isEmpty());
    }

    @Test
    public void testReadNoFields() {
        Assertions.assertTrue(customClass.getFields().isEmpty());
    }

    @Test
    public void testReadFunctions() {
        Assertions.assertFalse(customClass.getFunctions().isEmpty());
    }

    @Test
    public void testReadFieldName() {
        List<IFunction> functions = new ArrayList<>(customClass.getFunctions());
        if (functions.size() != 3) {
            Assertions.fail("3 functions should be found. However found " + functions.size());
        }
        functions.sort(Comparator.comparing(Nameable::getName));

        Assertions.assertEquals("parameterTest", functions.get(0).getName());
        Assertions.assertEquals("testHelloWorld", functions.get(1).getName());
        Assertions.assertEquals("testMethod", functions.get(2).getName());
    }

    @Test
    public void testReadMethodType() {
        List<IFunction> functions = new ArrayList<>(customClass.getFunctions());
        if (functions.size() != 3) {
            Assertions.fail("3 functions should be found. However found " + functions.size());
        }
        functions.sort(Comparator.comparing(Nameable::getName));

        Assertions.assertTrue(functions.get(0).getReturnType().getType() instanceof VoidType, "parameterTest should return a voidType however returns " + functions.get(0).getReturnType().getType().getName());
        Assertions.assertEquals("testHelloWorld", functions.get(1).getName());
        Assertions.assertTrue(functions.get(2).getReturnType().getType() instanceof VoidType, "testMethod should return a voidType however returns " + functions.get(0).getReturnType().getType().getName());

        ReturnType returnType = functions.get(1).getReturnType();
        Assertions.assertFalse(returnType.isArray(), "return type was found to be an array in testHelloWorld");
        IType type = returnType.getType();
        if (!(type instanceof BasicType basic)) {
            Assertions.fail("Return type was not of a basic type");
            return;
        }
        IClass targetClass = basic.getTargetClass();
        if (!(targetClass instanceof JavaClass jClass)) {
            Assertions.fail("Returning class is not String but instead: '" + targetClass.getFullName() + "'");
            return;
        }

        Assertions.assertEquals(String.class, jClass.getTargetClass());
    }

    @Test
    public void testReadMethodParameter() {
        List<IFunction> functions = new ArrayList<>(customClass.getFunctions());
        if (functions.size() != 3) {
            Assertions.fail("3 functions should be found. However found " + functions.size());
        }
        functions.sort(Comparator.comparing(Nameable::getName));

        Assertions.assertFalse(functions.get(0).getParameters().isEmpty());
        Assertions.assertTrue(functions.get(1).getParameters().isEmpty());
        Assertions.assertTrue(functions.get(2).getParameters().isEmpty());

        List<Parameter> parameters = functions.get(0).getParameters();
        Assertions.assertEquals(1, parameters.size());
        if (parameters.size() != 1) {
            return;
        }
        Parameter parameter = parameters.get(0);
        Assertions.assertEquals("param", parameter.getName());
        @NotNull StatedReturnType returnType = parameter.getReturnType();
        if (!(returnType.getType() instanceof BasicType basicType)) {
            Assertions.fail("Parameter is not String but instead: " + returnType.getType().getName());
            return;
        }
        IClass clazz = basicType.getTargetClass();
        if (!(clazz instanceof JavaClass jClass)) {
            Assertions.fail("Parameter is not a java string type");
            return;
        }
        Assertions.assertEquals(String.class, jClass.getTargetClass());
    }

    @Test
    public void testCodeBlock() {
        List<IFunction> functions = new ArrayList<>(customClass.getFunctions());
        if (functions.size() != 3) {
            Assertions.fail("3 functions should be found. However found " + functions.size());
        }
        functions.sort(Comparator.comparing(Nameable::getName));

        List<Codeable> codeBlock = functions.get(1).getCodeBlock();

        Assertions.assertTrue(functions.get(0).getCodeBlock().isEmpty());
        Assertions.assertFalse(codeBlock.isEmpty());
        Assertions.assertTrue(functions.get(2).getCodeBlock().isEmpty());
        Assertions.assertEquals(1, codeBlock.size());

        Codeable codeable = codeBlock.get(0);
        if (!(codeable instanceof ReturnLine returnLine)) {
            Assertions.fail("Code line is not ReturnLine but instead '" + codeable.getClass().getName() + "'");
            return;
        }

        Optional<Returnable.ReturnableLine> opReturnLine = returnLine.getLine();
        Assertions.assertTrue(opReturnLine.isPresent());

        Returnable.ReturnableLine line = opReturnLine.get();
        if (!(line instanceof StringConstructor stringConstructor)) {
            Assertions.fail("ReturnLine is not StringConstructor but instead " + line.getClass().getName());
            return;
        }
        Assertions.assertEquals("Hello World!", stringConstructor.getValue());
    }
}
