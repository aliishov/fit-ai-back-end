package org.raul.fit_ai.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification_tokens", schema = "auth")
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
		sequenceName = "auth.email_verification_tokens_seq", allocationSize = 50)
public class EmailVerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column(name = "user_id", nullable = false, updatable = false)
	UUID userId;

	@ToString.Exclude
	@Column(name = "otp_hash", nullable = false, updatable = false)
	String otpHash;

	@Builder.Default
	@Column(name = "verified", nullable = false)
	boolean verified = false;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	OffsetDateTime createdAt;

	@Column(name = "expires_at", nullable = false, updatable = false)
	OffsetDateTime expiresAt;

	@Column(name = "used_at")
	OffsetDateTime usedAt;

	public boolean isExpired() {
		return OffsetDateTime.now().isAfter(expiresAt);
	}

	public boolean isUsed() {
		return usedAt != null;
	}
}
