package com.firefox.center.sys.common.exception;

public class FirefoxException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FirefoxException(String message){
		super(message);
	}

	public FirefoxException(Throwable cause)
	{
		super(cause);
	}

	public FirefoxException(String message, Throwable cause)
	{
		super(message,cause);
	}

}
