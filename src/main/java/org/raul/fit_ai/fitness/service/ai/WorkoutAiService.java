package org.raul.fit_ai.fitness.service.ai;

import org.raul.fit_ai.common.exception.AiResponseParseException;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.UserProgress;
import org.raul.fit_ai.fitness.service.WorkoutService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("prod")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkoutAiService {

	ChatClient chatClient;
	ObjectMapper objectMapper;
	WorkoutService workoutService;

	public AiWorkoutPlanDTO  generatePLan(
			UserProfile profile,
			List<Exercise> exercises,
			List<UserProgress> progressHistory,
			Integer durationWeeks
	) {

		String prompt = buildPrompt(profile, exercises, progressHistory, durationWeeks);

		log.info("Calling Claude for userId=[{}] exerciseCount=[{}]",
				profile.getUserId(), exercises.size());

		String response = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

		log.info("Claude responded for userId=[{}]", profile.getUserId());

		return parseResponse(response);
	}

	private String buildPrompt(
			UserProfile profile,
			List<Exercise> exercises,
			List<UserProgress> progressHistory,
			Integer durationWeeks
	) {
		StringBuilder sb = new StringBuilder();

		sb.append("""
            You are a professional fitness coach. Create a personalized workout plan.
            
            IMPORTANT RULES:
            - Use ONLY exercises from the provided list below
            - Do NOT create new exercises
            - Reference exercises ONLY by their exact id from the list
            - Return ONLY valid JSON, no markdown, no explanation
            
            USER PROFILE:
            """);

		sb.append("- Gender: ").append(profile.getGender()).append("\n");
		sb.append("- Age: ").append(profile.getAge()).append("\n");
		sb.append("- Weight: ").append(profile.getWeightKg()).append(" kg\n");
		sb.append("- Height: ").append(profile.getHeightCm()).append(" cm\n");
		sb.append("- Fitness level: ").append(profile.getFitnessLevel()).append("\n");
		sb.append("- Goal: ").append(profile.getGoal()).append("\n");
		sb.append("- Activity type: ").append(profile.getActivityType()).append("\n");
		sb.append("- Sessions per week: ").append(profile.getSessionsPerWeek()).append("\n");
		sb.append("- Duration: ").append(durationWeeks).append(" weeks\n");

		if (profile.getLimitations() != null && !profile.getLimitations().isBlank()) {
			sb.append("- Physical limitations: ").append(profile.getLimitations()).append("\n");
			sb.append("  → Avoid exercises that stress the mentioned body parts\n");
		}

		if (!progressHistory.isEmpty()) {
			sb.append("\nPROGRESS HISTORY (most recent first):\n");
			progressHistory.stream().limit(3).forEach(p ->
					sb.append("- ").append(p.getRecordedAt().toLocalDate())
							.append(": ").append(p.getWeightKg()).append(" kg\n")
			);
		}

		sb.append("\nAVAILABLE EXERCISES:\n");
		sb.append("[");
		for (int i = 0; i < exercises.size(); i++) {
			Exercise e = exercises.get(i);
			sb.append("{")
					.append("\"id\":").append(e.getId()).append(",")
					.append("\"name\":\"").append(e.getName()).append("\",")
					.append("\"muscleGroup\":\"").append(e.getMuscleGroup()).append("\",")
					.append("\"difficulty\":\"").append(e.getDifficulty()).append("\"")
					.append("}");
			if (i < exercises.size() - 1) sb.append(",");
		}
		sb.append("]\n");

		sb.append("""
            
            REQUIRED JSON STRUCTURE (return exactly this format):
            {
              "aiNotes": "brief explanation of the plan",
              "weeks": [
                {
                  "weekNumber": 1,
                  "days": [
                    {
                      "dayNumber": 1,
                      "focus": "CHEST",
                      "notes": "brief day description",
                      "exercises": [
                        {
                          "exerciseId": <id from list above>,
                          "sets": 3,
                          "reps": 10,
                          "durationSeconds": null,
                          "restSeconds": 60,
                          "orderIndex": 1,
                          "notes": "optional tip"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            
            focus must be one of: CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY
            Generate exactly %d weeks with exactly %d training days per week.
            """.formatted(durationWeeks, profile.getSessionsPerWeek()));

		return sb.toString();
	}

	private AiWorkoutPlanDTO parseResponse(String response) {
		try {
			String clean = response
					.replaceAll("```json\\s*", "")
					.replaceAll("```\\s*", "")
					.trim();

			return objectMapper.readValue(clean, AiWorkoutPlanDTO.class);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse AI response: {}", response);
			throw new AiResponseParseException("Failed to parse workout plan from AI", e);
		}
	}
}
