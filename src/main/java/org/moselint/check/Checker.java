package org.moselint.check;

import org.moselint.exception.CheckException;
import org.openblock.creator.code.Codeable;

public interface Checker {

    boolean canCheck(Codeable codeable);

    void isValid(Codeable codeable) throws CheckException;

    String getDisplayName();
}
