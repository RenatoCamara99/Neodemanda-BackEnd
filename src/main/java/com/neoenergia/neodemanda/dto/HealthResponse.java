package com.neoenergia.neodemanda.dto;

/**
 * Resposta do health-check da API.
 *
 * @param status estado atual da aplicacao (ex.: {@code "ok"})
 */
public record HealthResponse(String status) {
}
