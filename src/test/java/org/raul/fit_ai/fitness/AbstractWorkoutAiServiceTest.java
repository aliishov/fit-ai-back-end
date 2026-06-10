package org.raul.fit_ai.fitness;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.raul.fit_ai.common.exception.AiResponseParseException;
import org.raul.fit_ai.fitness.dto.ai.AiWorkoutPlanDTO;
import org.raul.fit_ai.fitness.model.Exercise;
import org.raul.fit_ai.fitness.model.UserProfile;
import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;
import org.raul.fit_ai.fitness.service.ai.AbstractWorkoutAiService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWorkoutAiServiceTest {

    private ObjectMapper objectMapper;
    private AbstractWorkoutAiService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new AbstractWorkoutAiService(null, objectMapper, "test") {};
    }

    @Test
    void extractJsonObject_withCodeFence_returnsJson() throws Exception {
        String response = "Some intro\n```json\n{\"aiNotes\":\"ok\", \"weeks\": []}\n```\nfooter";

        Method extract = AbstractWorkoutAiService.class.getDeclaredMethod("extractJsonObject", String.class);
        extract.setAccessible(true);

        String json = (String) extract.invoke(service, response);
        assertTrue(json.contains("\"aiNotes\":\"ok\""));
        assertTrue(json.trim().startsWith("{"));
        assertTrue(json.trim().endsWith("}"));
    }

    @Test
    void extractJsonObject_noJson_throws() throws Exception {
        Method extract = AbstractWorkoutAiService.class.getDeclaredMethod("extractJsonObject", String.class);
        extract.setAccessible(true);

        assertThrows(AiResponseParseException.class, () -> {
            try {
                extract.invoke(service, "no json here");
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            }
        });
    }

    @Test
    void parseResponse_validJson_parses() throws Exception {
        String response = "{\"aiNotes\":\"generated\",\"weeks\":[]}";

        Method parse = AbstractWorkoutAiService.class.getDeclaredMethod("parseResponse", String.class);
        parse.setAccessible(true);

        AiWorkoutPlanDTO dto = (AiWorkoutPlanDTO) parse.invoke(service, response);
        assertNotNull(dto);
        assertEquals("generated", dto.aiNotes());
        assertNotNull(dto.weeks());
        assertEquals(0, dto.weeks().size());
    }

    @Test
    void parseResponse_invalidJson_throws() throws Exception {
        String response = "{invalid json}";

        Method parse = AbstractWorkoutAiService.class.getDeclaredMethod("parseResponse", String.class);
        parse.setAccessible(true);

        assertThrows(AiResponseParseException.class, () -> {
            try {
                parse.invoke(service, response);
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            }
        });
    }

    @Test
    void buildPrompt_containsSections() throws Exception {
        Method buildPrompt = AbstractWorkoutAiService.class.getDeclaredMethod(
                "buildPrompt",
                UserProfile.class, List.class, List.class, Integer.class
        );
        buildPrompt.setAccessible(true);

        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .activityType(ActivityType.FITNESS)
                .weightKg(BigDecimal.valueOf(70))
                .heightCm(175)
                .age(30)
                .gender(Gender.MALE)
                .goal(FitnessGoal.GENERAL)
                .fitnessLevel(FitnessLevel.BEGINNER)
                .sessionsPerWeek(3)
                .build();

        Exercise ex = Exercise.builder()
                .id(1L)
                .name("push")
                .muscleGroup(MuscleGroup.CHEST)
                .difficulty(FitnessLevel.BEGINNER)
                .build();

        String prompt = (String) buildPrompt.invoke(service, profile, List.of(ex), Collections.emptyList(), 4);

        assertTrue(prompt.contains("AVAILABLE EXERCISES"));
        assertTrue(prompt.contains("Sessions per week: 3"));
        assertTrue(prompt.contains("Duration: 4 weeks"));
        assertTrue(prompt.contains("REQUIRED JSON STRUCTURE"));
    }
}
