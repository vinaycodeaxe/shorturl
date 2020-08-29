package com.observe.shorturl.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

	private static final long serialVersionUID = -59926282956449508L;
	public static final String MESSAGE = "Data Already exits ";
	public static final String HAVING_ID = " having id ";


	public <T> ConflictException(T clazz, String id) {
		super(MESSAGE + clazz + HAVING_ID + id);
	}

	public <T> ConflictException(T clazz, Long id, Throwable t) {
		super(MESSAGE + clazz + HAVING_ID + id, t);

	}

}
