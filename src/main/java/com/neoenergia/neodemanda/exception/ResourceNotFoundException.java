package com.neoenergia.neodemanda.exception;

/**
 * Lancada quando um recurso solicitado nao existe.
 */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
