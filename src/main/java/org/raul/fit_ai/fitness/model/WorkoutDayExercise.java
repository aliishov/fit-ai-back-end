package org.raul.fit_ai.fitness.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "workout_day_exercises", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class WorkoutDayExercise {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workout_day_id", nullable = false, updatable = false)
	WorkoutDay workoutDay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exercise_id", nullable = false, updatable = false)
	Exercise exercise;

	@Column(name = "sets")
	Integer sets;

	@Column(name = "reps")
	Integer reps;

	@Column(name = "duration_seconds")
	Integer durationSeconds;

	@Column(name = "rest_seconds")
	Integer restSeconds;

	@Column(name = "order_index", nullable = false)
	Integer orderIndex;

	@Column(name = "notes", columnDefinition = "TEXT")
	String notes;
}
