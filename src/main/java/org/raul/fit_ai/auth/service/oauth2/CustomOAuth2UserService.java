package org.raul.fit_ai.auth.service.oauth2;

import org.raul.fit_ai.auth.model.AppUser;
import org.raul.fit_ai.auth.model.UserPrincipal;
import org.raul.fit_ai.auth.model.enumerated.AuthProvider;
import org.raul.fit_ai.auth.model.enumerated.Role;
import org.raul.fit_ai.auth.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final AppUserRepository appUserRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(
				registrationId, oAuth2User.getAttributes());

		if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
			log.warn("Email not provided by OAuth2 provider [{}]", registrationId);
			throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider");
		}

		if (!userInfo.isEmailVerified()) {
			log.warn("Email not verified for OAuth2 user [{}]", userInfo.getEmail());
			throw new OAuth2AuthenticationException("Email not verified by OAuth2 provider");
		}

		AppUser appUser = resolveUser(userInfo, registrationId);
		return new UserPrincipal(appUser, appUser.getEmail());
	}

	private AppUser resolveUser(OAuth2UserInfo userInfo, String registrationId) {
		AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

		return appUserRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
				.map(user -> updateExistingUser(user, userInfo))
				.or(() -> appUserRepository.findByEmail(userInfo.getEmail())
						.map(user -> linkProviderToExistingUser(user, userInfo, provider)))
				.orElseGet(() -> createNewUser(userInfo, provider));
	}

	private AppUser updateExistingUser(AppUser user, OAuth2UserInfo userInfo) {
		if (!user.isEnabled()) {
			throw new OAuth2AuthenticationException("Account is disabled");
		}

		user.setAvatarUrl(userInfo.getAvatarUrl());
		return appUserRepository.save(user);
	}

	private AppUser linkProviderToExistingUser(AppUser user,
	                                           OAuth2UserInfo userInfo,
	                                           AuthProvider provider) {
		if (!user.isEnabled()) {
			throw new OAuth2AuthenticationException("Account is disabled");
		}

		if (user.getProvider() == AuthProvider.LOCAL) {
			log.info("Linking {} provider to existing LOCAL user [{}]", provider, user.getId());
			user.setProvider(provider);
			user.setProviderId(userInfo.getProviderId());
			user.setAvatarUrl(userInfo.getAvatarUrl());
			user.setEmailVerified(true);
			return appUserRepository.save(user);
		}

		if (user.getProvider() != provider) {
			log.warn("Provider conflict for user [{}]: existing=[{}] incoming=[{}]",
					user.getId(), user.getProvider(), provider);
			throw new OAuth2AuthenticationException(
					"Account already linked to " + user.getProvider().name() + " provider");
		}

		return updateExistingUser(user, userInfo);
	}

	private AppUser createNewUser(OAuth2UserInfo userInfo, AuthProvider provider) {
		log.info("Creating new AppUser from OAuth2 provider [{}] email=[{}]",
				provider, userInfo.getEmail());

		AppUser newUser = AppUser.builder()
				.email(userInfo.getEmail())
				.firstName(userInfo.getFirstName())
				.lastName(userInfo.getLastName())
				.avatarUrl(userInfo.getAvatarUrl())
				.provider(provider)
				.providerId(userInfo.getProviderId())
				.role(Role.ROLE_APP_USER)
				.emailVerified(true)
				.enabled(true)
				.build();

		return appUserRepository.save(newUser);
	}

}
