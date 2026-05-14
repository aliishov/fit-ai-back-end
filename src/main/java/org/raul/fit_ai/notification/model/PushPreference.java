package org.raul.fit_ai.notification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "push_preferences", schema = "notifications")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "pp_seq",
		sequenceName = "notifications.push_preferences_seq", allocationSize = 50)
public class PushPreference {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Builder.Default
	@Column(name = "workout_reminder", nullable = false)
	boolean workoutReminder = true;

	@Builder.Default
	@Column(name = "water_reminder", nullable = false)
	boolean waterReminder = true;

	@Builder.Default
	@Column(name = "meal_reminder", nullable = false)
	boolean mealReminder = false;

	@Column(name = "workout_reminder_time")
	LocalTime workoutReminderTime;
}
