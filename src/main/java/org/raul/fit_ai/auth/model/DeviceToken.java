package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.DevicePlatform;

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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_tokens", schema = "auth")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeviceToken {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Column(name = "token", nullable = false, unique = true)
	String token;

	@Enumerated(EnumType.STRING)
	@Column(name = "platform", nullable = false)
	DevicePlatform platform;

	@Column(name = "is_active", nullable = false)
	boolean active = true;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	OffsetDateTime updatedAt;
}
