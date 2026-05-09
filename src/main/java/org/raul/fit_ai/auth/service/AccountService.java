package org.raul.fit_ai.auth.service;

import org.raul.fit_ai.auth.dto.request.ChangePasswordRequestDTO;
import org.raul.fit_ai.auth.dto.request.UpdateProfileRequestDTO;
import org.raul.fit_ai.auth.dto.response.AccountResponseDTO;
import org.raul.fit_ai.auth.mapper.AccountMapper;
import org.raul.fit_ai.auth.model.AdminUser;
import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.BaseUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.repository.AdminUserRepository;
import org.raul.fit_ai.auth.repository.AppUserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.raul.fit_ai.auth.service.jwt.JwtManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountService {

	AdminUserRepository adminUserRepository;
	AppUserRepository appUserRepository;
	AccountMapper accountMapper;
	PasswordManagementService passwordManagementService;
	JwtManager jwtManager;

	@Transactional(readOnly = true)
	public AccountResponseDTO getProfile(UserPrincipal principal) {
		log.info("Retrieving user info for [{}]", principal.getId());

		BaseUser user = principal.user();
		return accountMapper.toResponseDTO(user);
	}

	@Transactional
	public void updateProfile(UserPrincipal principal, UpdateProfileRequestDTO request) {
		log.info("Updating profile for user [{}]", principal.getId());

		BaseUser user = principal.user();

		if (request.firstName() != null) {
			user.setFirstName(request.firstName());
		}

		if (request.lastName() != null) {
			user.setLastName(request.lastName());
		}

		if (request.phone() != null) {
			user.setPhone(request.phone());
		}

		save(user);

		log.info("User [{}] updated profile", user.getId());
	}

	@Transactional
	public void updatePassword(UserPrincipal principal, ChangePasswordRequestDTO request) {
		log.info("Updating password for user [{}]", principal.getId());

		BaseUser user = principal.user();

		String oldPassword = request.oldPassword();
		String newPassword = request.newPassword();

		passwordManagementService.isPasswordMatches(oldPassword, user.getPasswordHash());
		passwordManagementService.validateNewPassword(oldPassword, newPassword);
		passwordManagementService.updatePassword(user, newPassword);

		save(user);

		jwtManager.revokeToken(request.refreshToken());
	}

	private void save(BaseUser user) {
		if (user instanceof AdminUser admin) {
			adminUserRepository.save(admin);
		} else if (user instanceof AppUser appUser) {
			appUserRepository.save(appUser);
		} else {
			throw new IllegalStateException("Unknown user type: " + user.getClass());
		}
	}
}
