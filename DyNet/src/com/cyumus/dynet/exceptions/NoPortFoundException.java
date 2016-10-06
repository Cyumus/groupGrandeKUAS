package com.cyumus.dynet.exceptions;

public class NoPortFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NoPortFoundException(String message){
		super(message);
	}
}
