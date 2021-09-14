package org.moselint.check;

import org.moselint.check.variable.field.FieldNameCheck;

public interface Checkers {

    FieldNameCheck FIELD_NAME = new FieldNameCheck();
}
