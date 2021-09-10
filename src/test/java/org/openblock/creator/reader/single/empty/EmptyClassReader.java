package org.openblock.creator.reader.single.empty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.code.Visibility;
import org.openblock.creator.code.clazz.ClassType;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.custom.clazz.standardtype.CustomStandardClass;
import org.openblock.creator.project.Project;

public class EmptyClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public class EmptyClassReader {",
            "}"};
    private static final Project<AbstractCustomClass> project = new Project<>("Test");
    private static final CustomClassReader reader = new CustomClassReader(classAsString);

    private static AbstractCustomClass customClass;

    @BeforeAll
    public static void init() {
        customClass = reader.readStageOne(project);
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
    public void testReadNoFunctions() {
        Assertions.assertTrue(customClass.getFunctions().isEmpty());
    }

}
