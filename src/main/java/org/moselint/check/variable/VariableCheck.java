package org.moselint.check.variable;

import org.moselint.check.Checker;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.variable.IVariable;

public interface VariableCheck extends Checker {

    @Override
    default boolean canCheck(Codeable codeable) {
        return codeable instanceof IVariable;
    }
}
