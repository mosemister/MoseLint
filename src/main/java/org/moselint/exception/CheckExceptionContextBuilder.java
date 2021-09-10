package org.moselint.exception;

import java.nio.file.Path;

public class CheckExceptionContextBuilder {

    private Path file;
    private int lineStartsOn;
    private int lineEndsOn;
    private int characterStartsAt;
    private int characterEndsAt;
    private String message;

    public String getMessage() {
        return message;
    }

    public CheckExceptionContextBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public Path getFile() {
        return file;
    }

    public CheckExceptionContextBuilder setFile(Path file) {
        this.file = file;
        return this;
    }

    public int getLineStartsOn() {
        return lineStartsOn;
    }

    public CheckExceptionContextBuilder setLineStartsOn(int lineStartsOn) {
        this.lineStartsOn = lineStartsOn;
        return this;
    }

    public int getLineEndsOn() {
        return lineEndsOn;
    }

    public CheckExceptionContextBuilder setLineEndsOn(int lineEndsOn) {
        this.lineEndsOn = lineEndsOn;
        return this;
    }

    public int getCharacterStartsAt() {
        return characterStartsAt;
    }

    public CheckExceptionContextBuilder setCharacterStartsAt(int characterStartsAt) {
        this.characterStartsAt = characterStartsAt;
        return this;
    }

    public int getCharacterEndsAt() {
        return characterEndsAt;
    }

    public CheckExceptionContextBuilder setCharacterEndsAt(int characterEndsAt) {
        this.characterEndsAt = characterEndsAt;
        return this;
    }
}
