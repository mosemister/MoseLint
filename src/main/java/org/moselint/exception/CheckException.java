package org.moselint.exception;

public class CheckException extends Exception {

    private final CheckExceptionContext[] context;

    @Deprecated
    public CheckException() {
        this(new CheckExceptionContext[0]);
    }

    public CheckException(CheckExceptionContext... contexts) {
        if (contexts.length == 0) {
            throw new RuntimeException("At least one CheckExceptionContext should be provided");
        }
        this.context = contexts;
    }

    public CheckExceptionContext[] getContext() {
        return this.context;
    }

    @Override
    public String getMessage() {
        if (this.context.length == 1) {
            return this.context[0].getMessage();
        }
        return "Multiple issues found in one check. Refer to output to see all issues";
    }
}
