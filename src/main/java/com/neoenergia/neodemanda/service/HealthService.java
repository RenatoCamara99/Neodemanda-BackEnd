package com.neoenergia.neodemanda.service;

import com.neoenergia.neodemanda.dto.HealthResponse;
import org.springframework.stereotype.Service;

/**
 * Regra de negocio do health-check da aplicacao.
 */
@Service
public class HealthService {

	private static final String STATUS_OK = "ok";

	/**
	 * @return o estado atual da aplicacao
	 */
	public HealthResponse check() {
		return new HealthResponse(STATUS_OK);
	}

}
