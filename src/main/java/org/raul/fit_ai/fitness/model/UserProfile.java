package org.raul.fit_ai.fitness.model;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessGoal;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(name = "user_id", nullable = false, updatable = false, unique = true)
	UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "activity_type", nullable = false)
	ActivityType activityType;

	@Column(name = "weight_kg", nullable = false)
	BigDecimal weightKg;

	@Column(name = "height_cm", nullable = false)
	BigDecimal heightCm;

	@Column(name = "age", nullable = false)
	Integer age;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false)
	Gender gender;

	@Enumerated(EnumType.STRING)
	@Column(name = "goal", nullable = false)
	FitnessGoal goal;

	@Enumerated(EnumType.STRING)
	@Column(name = "fitness_level", nullable = false)
	FitnessLevel fitnessLevel;

	@Column(name = "sessions_per_week", nullable = false)
	Integer sessionsPerWeek;

	@Column(name = "limitations", columnDefinition = "TEXT")
	String limitations;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	OffsetDateTime updatedAt;
}
