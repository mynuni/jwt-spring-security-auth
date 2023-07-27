package com.study.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.study.auth.domain.User;
import com.study.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User foundUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음 " + email));
		
		return new UserDetailsImpl(foundUser);
	}

}
