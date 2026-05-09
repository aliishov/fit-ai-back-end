package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Table(name = "admin_users", schema = "auth")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class AdminUser extends BaseUser {

	@Column(name = "created_by", updatable = false)
	UUID createdBy;

	@PrePersist
	private void ensureAdminRole() {
		setRole(Role.ROLE_ADMIN);
	}
}
