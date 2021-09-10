package org.moselint.exception;

import java.nio.file.Path;

public class CheckExceptionContext {

    private final Path file;
    private final int lineStartsOn;
    private final int lineEndsOn;
    private final int characterStartsAt;
    private final int characterEndsAt;
    private final String message;

    public CheckExceptionContext(CheckExceptionContextBuilder builder) {
        this.file = builder.getFile();
        this.lineEndsOn = builder.getLineEndsOn();
        this.lineStartsOn = builder.getLineStartsOn();
        this.characterEndsAt = builder.getCharacterEndsAt();
        this.characterStartsAt = builder.getCharacterStartsAt();
        this.message = builder.getMessage();
    }

    public String getMessage() {
        return this.message;
    }

    public Path getFile() {
        return file;
    }

    public int getLineStartsOn() {
        return lineStartsOn;
    }

    public int getLineEndsOn() {
        return lineEndsOn;
    }

    public int getCharacterStartsAt() {
        return characterStartsAt;
    }

    public int getCharacterEndsAt() {
        return characterEndsAt;
    }
}
