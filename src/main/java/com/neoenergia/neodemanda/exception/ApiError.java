package com.neoenergia.neodemanda.exception;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Corpo padrao das respostas de erro da API.
 *
 * @param timestamp momento em que o erro ocorreu
 * @param status    codigo HTTP
 * @param error     descricao curta do codigo HTTP
 * @param message   detalhe legivel do erro
 * @param path      recurso que originou o erro
 * @param fields    erros por campo; vazio fora das falhas de validacao
 */
public record ApiError(
		OffsetDateTime timestamp,
		int status,
		String error,
		String message,
		String path,
		Map<String, String> fields) {

	public static ApiError of(int status, String error, String message, String path) {
		return new ApiError(OffsetDateTime.now(), status, error, message, path, Map.of());
	}

	public static ApiError ofValidation(int status, String error, String message, String path,
			Map<String, String> fields) {
		return new ApiError(OffsetDateTime.now(), status, error, message, path, Map.copyOf(fields));
	}

}
