package edu.java.bot.exceptions;

import com.github.tomakehurst.wiremock.admin.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebServerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setExceptionMessage(e.getMessage());
        apiErrorResponse.setExceptionName(e.toString());
        apiErrorResponse.setDescription("Внутренняя ошибка сервера");
        apiErrorResponse.setCode("500");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(NotFoundException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setExceptionMessage(e.getMessage());
        apiErrorResponse.setExceptionName(e.getClass().getName());
        apiErrorResponse.setDescription("Некорректные параметры запроса");
        apiErrorResponse.setCode("400");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setExceptionMessage(e.getMessage());
        apiErrorResponse.setExceptionName(e.getClass().getName());
        apiErrorResponse.setDescription("Ссылка не найдена");
        apiErrorResponse.setCode("404");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }
}
