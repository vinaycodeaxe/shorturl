package com.observe.shorturl.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoDataFoundException extends RuntimeException {

	private static final long serialVersionUID = -5318591489806278624L;

	private String message;
	private String code;


	public NoDataFoundException(String message) {
		super(message);
		this.message = message;
	}

	public NoDataFoundException(String message, String code) {
		super(message);
		this.message = message;
		this.code = code;
	}

	public NoDataFoundException(String message, Throwable t) {
		super(message, t);
		this.message = message;
	}

}
