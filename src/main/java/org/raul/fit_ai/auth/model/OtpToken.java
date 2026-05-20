package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.OtpType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_tokens", schema = "auth")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "prt_seq",
		sequenceName = "auth.otp_tokens_seq", allocationSize = 50)
public class OtpToken {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, updatable = false)
	OtpType type;

	@Column(name = "otp_hash", nullable = false, updatable = false)
	String otpHash;

	@Column(name = "expires_at", nullable = false, updatable = false)
	OffsetDateTime expiresAt;

	@Column(name = "verified", nullable = false)
	boolean verified = false;

	@Column(name = "used_at")
	OffsetDateTime usedAt;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	OffsetDateTime createdAt;

	public boolean isExpired() {
		return OffsetDateTime.now().isAfter(expiresAt);
	}

	public boolean isUsed() {
		return usedAt != null;
	}
}
