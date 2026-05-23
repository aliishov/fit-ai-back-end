package org.raul.fit_ai.fitness.model;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.PlanStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_plans", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class WorkoutPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "activity_type", nullable = false)
	ActivityType activityType;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	PlanStatus status;

	@Column(name = "duration_weeks", nullable = false)
	Integer durationWeeks;

	@Column(name = "sessions_per_week", nullable = false)
	Integer sessionsPerWeek;

	@Column(name = "starts_at", nullable = false)
	LocalDate startsAt;

	@Column(name = "ends_at", nullable = false)
	LocalDate endsAt;

	@Column(name = "ai_notes", columnDefinition = "TEXT")
	String aiNotes;

	@OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL)
	List<WorkoutWeek> weeks = new ArrayList<>();

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	OffsetDateTime updatedAt;
}
