package edu.java.exception;

import lombok.Getter;

@Getter
public class ScrapperException extends RuntimeException {
    protected final String description;

    public ScrapperException(String message, String description) {
        super(message);
        this.description = description;
    }
}
