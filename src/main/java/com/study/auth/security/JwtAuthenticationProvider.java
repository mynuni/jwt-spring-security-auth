package com.study.auth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String principal = (String) authentication.getPrincipal();
		String name = (String) authentication.getName();
		String password = (String) authentication.getCredentials();

		UserDetails userDetails = userDetailsService.loadUserByUsername(name);
		
		if (userDetails == null) {
			throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
		}

		if (!password.equals(userDetails.getPassword())) {
			throw new BadCredentialsException("비밀번호가 틀렸습니다.");
		}
		
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), password, userDetails.getAuthorities());

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
