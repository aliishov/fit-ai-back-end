package org.raul.fit_ai.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

	@Bean
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder
				.defaultSystem("""
                    You are a professional fitness and pilates coach with 10+ years of experience.
                    You create safe, effective, personalized workout plans.
                    Always respond with valid JSON only — no markdown, no explanations outside JSON.
                    """)
				.build();
	}
}
