package edu.java.bot.controller;

import edu.java.bot.exception.BotException;
import edu.java.bot.exception.InternalServerBotException;
import edu.java.bot.exception.NotFoundBotException;
import edu.java.bot.exception.RateLimitBotException;
import java.util.Arrays;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.example.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebServerExceptionHandler {

    @ExceptionHandler(InternalServerBotException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getDescription(), e);

    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getDescription(), e);
    }

    @ExceptionHandler(NotFoundBotException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, e.getDescription(), e);

    }

    @ExceptionHandler(RateLimitBotException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiErrorResponse handleRateLimitException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS, e.getDescription(), e);
    }

    private ApiErrorResponse buildErrorResponse(String message, HttpStatus status, String description, Exception e) {
        return new ApiErrorResponse(
            message,
            status.toString(),
            e.getClass().getName(),
            description,
            getListStackTrace(e)
        );
    }

    private List<String> getListStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList();
    }
}
