package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.Role;
import org.raul.fit_ai.auth.model.enumerated.AuthProvider;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUser {

	@Id
	@GeneratedValue(strategy =  jakarta.persistence.GenerationType.UUID)
	@ToString.Include
	@Column(name = "id", updatable = false)
	UUID id;

	@ToString.Include
	@Column(name = "first_name")
	String firstName;

	@ToString.Include
	@Column(name = "last_name")
	String lastName;

	@ToString.Include
	@Column(name = "email", unique = true)
	String email;

	@ToString.Include
	@Column(name = "phone", unique = true)
	String phone;

	@Column(name = "avatar_url", columnDefinition = "TEXT")
	String avatarUrl;

	@Column(name = "password_hash", columnDefinition = "TEXT")
	String passwordHash;

	@Column(name = "provider_id")
	String providerId;

	@ToString.Include
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	AuthProvider provider = AuthProvider.LOCAL;

	@ToString.Include
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	Role role = Role.ROLE_APP_USER;

	@ToString.Include
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	OffsetDateTime updatedAt;

	@Column(name = "deleted_at")
	OffsetDateTime deletedAt;

	@Column(name = "last_sign_in")
	OffsetDateTime lastSignIn;

	@ToString.Include
	@Builder.Default
	@Column(name = "is_enabled", nullable = false)
	boolean enabled = true;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy hp
				? hp.getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy hp
				? hp.getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		BaseUser that = (BaseUser) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy hp
				? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
