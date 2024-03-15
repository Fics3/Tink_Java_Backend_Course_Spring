package edu.java.bot.controller;

import com.github.tomakehurst.wiremock.admin.NotFoundException;
import edu.java.bot.exception.BotException;
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getDescription(), e);

    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getDescription(), e);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(BotException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, e.getDescription(), e);

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
