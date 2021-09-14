package org.moselint.check.variable.field;

import org.moselint.check.variable.VariableCheck;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.variable.field.Field;

public interface FieldCheck extends VariableCheck {

    @Override
    default boolean canCheck(Codeable codeable) {
        return codeable instanceof Field;
    }
}
