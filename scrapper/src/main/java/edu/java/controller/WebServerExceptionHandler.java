package edu.java.controller;

import edu.java.exception.BadRequestScrapperException;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.exception.InternalServerScrapperException;
import edu.java.exception.NotFoundScrapperException;
import edu.java.exception.RateLimitScrapperException;
import edu.java.exception.ScrapperException;
import java.util.Arrays;
import java.util.List;
import org.example.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebServerExceptionHandler {

    @ExceptionHandler(InternalServerScrapperException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(ScrapperException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getDescription(), e);
    }

    @ExceptionHandler(BadRequestScrapperException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(ScrapperException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getDescription(), e);
    }

    @ExceptionHandler(NotFoundScrapperException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(ScrapperException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, e.getDescription(), e);
    }

    @ExceptionHandler(DuplicateRegistrationScrapperException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateRegistrationException(ScrapperException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, e.getDescription(), e);
    }

    @ExceptionHandler(DuplicateLinkScrapperException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateLinkException(ScrapperException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, e.getDescription(), e);
    }

    @ExceptionHandler(RateLimitScrapperException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiErrorResponse handleRateLimitException(ScrapperException e) {
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
