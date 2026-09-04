package com.neoenergia.neodemanda.controller;

import com.neoenergia.neodemanda.dto.HealthResponse;
import com.neoenergia.neodemanda.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de verificacao de disponibilidade da API.
 */
@RestController
@RequestMapping("/health")
public class HealthController {

	private final HealthService healthService;

	public HealthController(HealthService healthService) {
		this.healthService = healthService;
	}

	@GetMapping
	public ResponseEntity<HealthResponse> health() {
		return ResponseEntity.ok(healthService.check());
	}

}
