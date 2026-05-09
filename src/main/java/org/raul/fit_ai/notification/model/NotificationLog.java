package org.raul.fit_ai.notification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.raul.fit_ai.notification.model.enumerated.NotificationChannel;
import org.raul.fit_ai.notification.model.enumerated.NotificationStatus;
import org.raul.fit_ai.notification.model.enumerated.NotificationType;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_logs", schema = "notifications")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "channel", nullable = false, updatable = false)
	NotificationChannel channel;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, updatable = false)
	NotificationType type;

	@Column(name = "recipient", nullable = false, updatable = false)
	String recipient;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	NotificationStatus status;

	@Column(name = "error_message")
	String errorMessage;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	OffsetDateTime createdAt
}
