package org.raul.fit_ai.auth.model;

import org.raul.fit_ai.auth.model.enumerated.Role;

import org.jspecify.annotations.NonNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record UserPrincipal(BaseUser user, String identifier) implements UserDetails {

	public UserPrincipal(BaseUser user) {
		this(user, user.getEmail());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPasswordHash();
	}

	@Override
	public @NonNull String getUsername() {
		return identifier;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	public UUID getId() {
		return user.getId();
	}

	public Role getRole() {
		return user.getRole();
	}
}
