package org.openblock.creator.reader.single.empty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.project.Project;

public class AbstractFinalEmptyClassReader {

    private static final String[] classAsString = {"package org.openblock.creator.reader;",
            "",
            "public abstract final class EmptyClassReader {",
            "}"};
    private static final Project<AbstractCustomClass> project = new Project<>("Test");
    private static final CustomClassReader reader = new CustomClassReader(classAsString);

    @Test
    public void testReadFail() {
        try {
            reader.readStageOne(project);
            Assertions.fail("abstract final classes are not part of Java");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Cannot have a class as abstract and final", e.getMessage());
        }
    }


}
