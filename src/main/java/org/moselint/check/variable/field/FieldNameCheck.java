package org.moselint.check.variable.field;

import org.moselint.exception.CheckException;
import org.moselint.exception.CheckExceptionContext;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.variable.field.Field;

public class FieldNameCheck implements FieldCheck {
    @Override
    public void isValid(Codeable codeable) throws CheckException {
        if (!canCheck(codeable)) {
            throw new RuntimeException("Failed to do checks before hand");
        }
        Field field = (Field) codeable;
        String name = field.getName();
        if (field.isFinal() && field.isStatic()) {
            if (name.chars().anyMatch(Character::isLowerCase)) {
                CheckExceptionContext context = new CheckExceptionContext("static final fields should use all uppercase names separated by underscores");
                throw new CheckException(context);
            }
            return;
        }
        if (name.contains("_")) {
            CheckExceptionContext context = new CheckExceptionContext("underscore should not be used in variable names unless it is final static");
            throw new CheckException(context);
        }

    }
}
