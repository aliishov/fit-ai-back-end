package org.raul.fit_ai.fitness.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
public class OllamaWorkoutAiService extends AbstractWorkoutAiService {

	public OllamaWorkoutAiService(ChatClient chatClient, ObjectMapper objectMapper) {
		super(chatClient, objectMapper, "Ollama");
	}
}
