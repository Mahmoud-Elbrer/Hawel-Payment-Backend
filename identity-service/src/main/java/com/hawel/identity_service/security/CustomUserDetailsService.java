package com.hawel.identity_service.security;



import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.hawel.identity_service.security.filter.UserPrincipal;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserPrincipal loadUserByUsername(String username)
			throws UsernameNotFoundException {

		User user = userRepository.findByEmail(username)
				.orElseThrow(() ->
						new UsernameNotFoundException("User not found."));

		return new UserPrincipal(user);
	}

	public UserPrincipal loadUserByUserId(UUID userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() ->
						new UsernameNotFoundException("User not found."));

		return new UserPrincipal(user);
	}


}