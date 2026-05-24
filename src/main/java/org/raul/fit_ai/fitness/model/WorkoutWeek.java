package org.raul.fit_ai.fitness.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "workout_weeks", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class WorkoutWeek {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workout_plan_id", nullable = false, updatable = false)
	WorkoutPlan workoutPlan;

	@Column(name = "week_number", nullable = false)
	Integer weekNumber;

	@OneToMany(mappedBy = "workoutWeek", cascade = CascadeType.ALL)
	List<WorkoutDay> days = new ArrayList<>();
}
