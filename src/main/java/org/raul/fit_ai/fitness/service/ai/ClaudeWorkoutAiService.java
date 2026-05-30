package org.raul.fit_ai.fitness.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ClaudeWorkoutAiService extends AbstractWorkoutAiService {

	public ClaudeWorkoutAiService(ChatClient chatClient, ObjectMapper objectMapper) {
		super(chatClient, objectMapper, "Claude");
	}
}
