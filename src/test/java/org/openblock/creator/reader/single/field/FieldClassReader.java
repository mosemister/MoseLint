package org.openblock.creator.reader.single.field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.code.Visibility;
import org.openblock.creator.code.call.Returnable;
import org.openblock.creator.code.call.returntype.ReturnType;
import org.openblock.creator.code.clazz.ClassType;
import org.openblock.creator.code.clazz.type.BasicType;
import org.openblock.creator.code.line.primitive.StringConstructor;
import org.openblock.creator.code.variable.field.Field;
import org.openblock.creator.code.variable.field.InitiatedField;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.custom.clazz.standardtype.CustomStandardClass;
import org.openblock.creator.impl.java.clazz.JavaClass;
import org.openblock.creator.project.Project;

public class FieldClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public class EmptyClassReader {",
            "\tString helloWorld = \"Hello World\";",
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
    public void testReadFields() {
        Assertions.assertEquals(1, customClass.getFields().size());
    }

    @Test
    public void testReadNoFunctions() {
        Assertions.assertTrue(customClass.getFunctions().isEmpty());
    }

    @Test
    public void testReadFieldName() {
        if (customClass.getFields().isEmpty()) {
            Assertions.fail("Fields cannot be empty");
            return;
        }
        Field field = customClass.getFields().get(0);

        Assertions.assertEquals("helloWorld", field.getName());
    }

    @Test
    public void testReadFieldType() {
        if (customClass.getFields().isEmpty()) {
            Assertions.fail("Fields cannot be empty");
            return;
        }
        Field field = customClass.getFields().get(0);
        ReturnType returning = field.getReturnType();

        Assertions.assertFalse(returning.isArray());
        Assertions.assertTrue(returning.getType() instanceof BasicType);

        BasicType type = (BasicType) returning.getType();

        Assertions.assertEquals(new JavaClass(String.class), type.getTargetClass());
    }

    @Test
    public void testReadFieldValue() {
        if (customClass.getFields().isEmpty()) {
            Assertions.fail("Fields cannot be empty");
            return;
        }
        Field field = customClass.getFields().get(0);

        if (!(field instanceof InitiatedField iField)) {
            Assertions.fail("field must be initiated");
            return;
        }

        Returnable.ReturnableLine code = iField.getCode();
        if (!(code instanceof StringConstructor stringLine)) {
            Assertions.fail("field should be a StringConstructor, yet found " + code.getClass().getName());
            return;
        }

        Assertions.assertEquals("Hello World", stringLine.getValue());
    }

}
