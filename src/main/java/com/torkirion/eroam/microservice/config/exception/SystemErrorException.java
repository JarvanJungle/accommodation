package com.torkirion.eroam.microservice.config.exception;

public class SystemErrorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int code;
	public SystemErrorException(int code, String message) {
		super(message);
		this.code = code;
	}
	public SystemErrorException(int code, String message, Throwable throwable) {
		super(message, throwable);
		this.code = code;
	}
	public SystemErrorException(Type type) {
		this(type.code, type.message);
	}


	public int getCode() {
		return code;
	}

	public enum Type {
		AUTHENTICATION(403, "User or password incorect"),
		INVALID_INPUT_DATA(400, "Data invalid"),
		ERROR_SQL_EXCEPTION(401, "Service is unavailable, please try again later"),
		ERROR_UNHANDLE(503, "Service is unavailable, please try again later");

		private String message;
		private int code;

		Type(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public String message() {
			return message;
		}

		public int code() {
			return code;
		}
		public SystemErrorException toException() {
			return new SystemErrorException(this);
		}
	}
}
