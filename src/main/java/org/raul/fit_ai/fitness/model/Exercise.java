package org.raul.fit_ai.fitness.model;

import org.raul.fit_ai.fitness.model.enumerated.ActivityType;
import org.raul.fit_ai.fitness.model.enumerated.FitnessLevel;
import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "exercises", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Exercise {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "description", columnDefinition = "TEXT")
	String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "activity_type", nullable = false)
	ActivityType activityType;

	@Enumerated(EnumType.STRING)
	@Column(name = "muscle_group", nullable = false)
	MuscleGroup muscleGroup;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty", nullable = false)
	FitnessLevel difficulty;

	@Column(name = "equipment_needed")
	String equipmentNeeded;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	OffsetDateTime createdAt;
}
