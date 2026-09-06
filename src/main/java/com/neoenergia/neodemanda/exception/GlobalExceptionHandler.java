package com.neoenergia.neodemanda.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduz excecoes da aplicacao para respostas JSON padronizadas ({@link ApiError}).
 *
 * <p>Excecoes nao mapeadas aqui continuam sendo tratadas pelo handler padrao do
 * Spring Boot, preservando os status originais do framework (por exemplo, 404
 * para rotas inexistentes).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Falha de {@code @Valid} em corpo de requisicao. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> fields = new LinkedHashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			fields.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return validationError(fields, request);
	}

	/** Falha de {@code @Validated} em parametros de metodo. */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		Map<String, String> fields = new LinkedHashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			fields.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return validationError(fields, request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiError.of(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(),
						request.getRequestURI()));
	}

	private ResponseEntity<ApiError> validationError(Map<String, String> fields, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiError.ofValidation(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
						"Erro de validacao nos dados enviados", request.getRequestURI(), fields));
	}

}
