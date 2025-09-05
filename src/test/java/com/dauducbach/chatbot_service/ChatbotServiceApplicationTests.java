package com.dauducbach.chatbot_service;

import com.dauducbach.chatbot_service.configuration.GeminiEmbeddingClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ChatbotServiceApplicationTests {

	@MockitoBean
	public GeminiEmbeddingClient geminiEmbeddingClient;

	@Test
	void contextLoads() {
	}

}
