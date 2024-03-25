package edu.java.bot.exception;

import lombok.Getter;

@Getter
public class BotException extends RuntimeException {
    protected final String description;

    public BotException(String message, String description) {
        super(message);
        this.description = description;
    }

}
