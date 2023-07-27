package com.study.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = extractToken(request);
		log.info("토큰 {}", token);

		if (token != null && !jwtTokenProvider.isBlacklisted(token)) {
			if (jwtTokenProvider.isValidAccessToken(token)) {
				log.info("ACCESS 토큰으로 접근");
				String email = jwtTokenProvider.extractEmailFromToken(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
				return;
			}

			if (jwtTokenProvider.isValidRefreshToken(token)) {
				log.info("REFRESH 토큰으로 접근");
				String email = jwtTokenProvider.extractEmailFromToken(token);
				String newAccessToken = jwtTokenProvider.generateAccessToken(email);
				response.setHeader(AUTHORIZATION_HEADER, newAccessToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest httpServletRequest) {
		String authHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX.length());
		}
		return null;
	}

}
