package com.neoenergia.neodemanda.service;

import com.neoenergia.neodemanda.dto.HealthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthServiceTest {

	private final HealthService healthService = new HealthService();

	@Test
	@DisplayName("check() reporta a aplicacao como ok")
	void checkReturnsOk() {
		HealthResponse response = healthService.check();

		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo("ok");
	}

}
