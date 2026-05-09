package org.raul.fit_ai.auth.mapper;

import org.raul.fit_ai.auth.dto.response.AccountResponseDTO;
import org.raul.fit_ai.auth.model.BaseUser;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

	public AccountResponseDTO toResponseDTO(BaseUser user) {
		return new AccountResponseDTO(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getPhone(),
				user.getAvatarUrl()
		);
	}
}
