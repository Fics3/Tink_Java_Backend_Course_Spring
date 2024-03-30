package edu.java.exception;

public class RateLimitScrapperException extends ScrapperException {
    public RateLimitScrapperException(String message, String description) {
        super(message, description);
    }
}
