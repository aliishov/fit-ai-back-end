package org.raul.fit_ai.fitness.dto.response;

public record InitResponseDTO(
		boolean hasProfile,
		boolean profileComplete,
		boolean hasActivePlan,
		boolean hasGeneratingPlan,
		boolean hasPlanNeedingReview,
		boolean canGeneratePlan
) {
}
