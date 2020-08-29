package com.observe.shorturl.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Provides handling for exceptions throughout this service.
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> badRequestExceptionHandle(Exception exception) {
        return handleException(exception);
    }

    private ResponseEntity<ErrorResponse> handleException(Exception exception) {
        List<String> errorMessages = Collections.singletonList(exception.getMessage());
        String response = String.join(",", errorMessages);
        return new ResponseEntity<>(ErrorResponse.builder().status("failure").reason(response).build(), HttpStatus.BAD_REQUEST);
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private ResponseEntity<ErrorResponse> processFieldErrors(List<FieldError> fieldErrors) {

        String message = fieldErrors.stream().map(this::mapToMessage).collect(Collectors.joining(";"));


        return ResponseEntity.badRequest().body(ErrorResponse.builder().status("failure").reason(message).build());


    }

    private String mapToMessage(FieldError fieldError) {
        return fieldError.getField() + " " + (fieldError.getDefaultMessage() == null && fieldError.getDefaultMessage().length() == 0 ? "is Mandatory" : fieldError.getDefaultMessage());
    }
}
