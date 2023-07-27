package com.study.auth.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.study.auth.dto.TokenDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public TokenDto authenticateUser(String email, String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String accessToken = jwtTokenProvider.generateAccessToken(email);
		String refreshToken = jwtTokenProvider.generateRefreshToken(email);

		return new TokenDto(accessToken, refreshToken);
	}
}
