package edu.java.bot.exception;

public class RateLimitBotException extends BotException {
    public RateLimitBotException(String message, String description) {
        super(message, description);
    }
}
