package com.observe.shorturl.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoDataFoundException extends RuntimeException {

	private static final long serialVersionUID = -59926282956449508L;
	public static final String MESSAGE = "No Data found for ";
	public static final String HAVING_ID = " having id ";


	public <T> NoDataFoundException(T clazz, Long id) {
		super(MESSAGE + clazz + HAVING_ID + id);
	}

	public <T> NoDataFoundException(T clazz, Long id, Throwable t) {
		super(MESSAGE + clazz + HAVING_ID + id, t);

	}

}
