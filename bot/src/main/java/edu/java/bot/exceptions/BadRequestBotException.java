package edu.java.bot.exceptions;

public class BadRequestBotException extends BotException {
    public BadRequestBotException(String message, String description) {
        super(message, description);
    }
}
