package com.observe.shorturl.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -5318591489806278624L;

    private String message;
    private String code;


    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, String code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message, Throwable t) {
        super(message, t);
        this.message = message;
    }


}