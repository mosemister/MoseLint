package org.moselint.exception;

import org.jetbrains.annotations.NotNull;
import org.openblock.creator.code.Codeable;

import java.util.ArrayList;
import java.util.List;

public class CheckExceptionContext {

    private final List<Codeable> errors = new ArrayList<>();
    private final List<Codeable> suggestions = new ArrayList<>();
    private final String message;

    public CheckExceptionContext(@NotNull String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public List<Codeable> getSuggestions() {
        return this.suggestions;
    }

    public List<Codeable> getErrors() {
        return this.errors;
    }
}
