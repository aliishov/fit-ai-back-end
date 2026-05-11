package org.raul.fit_ai.notification.model;

import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_templates", schema = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, updatable = false)
	NotificationType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "channel", nullable = false, updatable = false)
	NotificationChannel channel;

	@Column(name = "subject")
	String subject;

	@Column(name = "body", nullable = false, columnDefinition = "TEXT")
	String body;

	@Column(name = "is_active", nullable = false)
	boolean active = true;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	OffsetDateTime updatedAt;
}
