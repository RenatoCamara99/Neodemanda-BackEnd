package com.neoenergia.neodemanda.controller;

import com.neoenergia.neodemanda.dto.HealthResponse;
import com.neoenergia.neodemanda.service.HealthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HealthService healthService;

	@Test
	@DisplayName("GET /health retorna 200 com {\"status\":\"ok\"}")
	void healthReturnsOk() throws Exception {
		given(healthService.check()).willReturn(new HealthResponse("ok"));

		mockMvc.perform(get("/health"))
				.andExpect(status().isOk())
				.andExpect(content().json("{\"status\":\"ok\"}"))
				.andExpect(jsonPath("$.status").value("ok"));

		verify(healthService).check();
	}

}
