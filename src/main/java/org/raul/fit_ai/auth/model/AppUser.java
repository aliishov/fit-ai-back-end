package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.AuthProvider;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "app_users", schema = "auth")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class AppUser extends BaseUser {

	@Builder.Default
	@Column(name = "is_email_verified", nullable = false)
	boolean emailVerified = false;

	@Column(name = "is_phone_verified")
	Boolean phoneVerified;

	@ToString.Include
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	AuthProvider provider = AuthProvider.LOCAL;

	@Column(name = "provider_id")
	String providerId;
}
