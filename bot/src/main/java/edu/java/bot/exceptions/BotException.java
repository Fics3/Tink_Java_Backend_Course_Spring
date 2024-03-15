package edu.java.bot.exceptions;

import lombok.Getter;

@Getter
public class BotException extends RuntimeException {
    protected String description;

    public BotException(String message, String description) {
        super(message);
        this.description = description;
    }

}
