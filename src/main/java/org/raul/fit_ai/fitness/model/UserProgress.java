package org.raul.fit_ai.fitness.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_progress", schema = "fitness")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class UserProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Column(name = "weight_kg", nullable = false, precision = 5, scale = 2)
	BigDecimal weightKg;

	@Column(name = "height_cm", nullable = false)
	Integer heightCm;

	@Column(name = "notes", columnDefinition = "TEXT")
	String notes;

	@Column(name = "plan_id", nullable = false, updatable = false)
	UUID planId;

	@CreatedDate
	@Column(name = "recorded_at", nullable = false, updatable = false)
	OffsetDateTime recordedAt;
}
