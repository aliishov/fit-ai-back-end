package org.raul.fit_ai.fitness.model;

import org.raul.fit_ai.fitness.model.enumerated.MuscleGroup;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_days", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class WorkoutDay {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workout_week_id", nullable = false, updatable = false)
	WorkoutWeek workoutWeek;

	@Column(name = "day_number", nullable = false)
	Integer dayNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "focus", nullable = false)
	MuscleGroup focus;

	@Column(name = "notes", columnDefinition = "TEXT")
	String notes;

	@OneToMany(mappedBy = "workoutDay", cascade = CascadeType.ALL)
	List<WorkoutDayExercise> exercises = new ArrayList<>();
}
