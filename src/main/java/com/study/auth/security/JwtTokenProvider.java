package com.study.auth.security;

import java.security.Key;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.study.auth.service.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private final RedisService redisService;

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.access-token-exp}")
	private Long accessTokenExpTime;

	@Value("${jwt.refresh-token-exp}")
	private Long refreshTokenExpTime;

	
	// Signin key 생성
	private Key generateSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Access token 생성
	public String generateAccessToken(String email) {
		Date now = new Date();
		return Jwts.builder()
				.claim("type", "access")
				.setSubject(email)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + accessTokenExpTime))
				.signWith(generateSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	// Refresh token 생성
	public String generateRefreshToken(String email) {
		Date now = new Date();
		String refreshToken = Jwts.builder()
				.claim("type", "refresh")
				.setSubject(email)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + refreshTokenExpTime))
				.signWith(generateSignInKey(), SignatureAlgorithm.HS256)
				.compact();

		redisService.addRefreshToken(refreshToken, email);
		return refreshToken;
	}
	
	
	// Access token 검증
	public boolean isValidAccessToken(String accessToken) {
	    try {
	        Claims claims = Jwts.parserBuilder()
	        		.setSigningKey(generateSignInKey())
	        		.build()
	        		.parseClaimsJws(accessToken)
	        		.getBody();
	        
	        if (!(claims.get("type").equals("access"))) {
	            return false;
	        } 
	        return claims.getExpiration().after(new Date());
	        
	    } catch (ExpiredJwtException e) {
	    	log.info("만료된 토큰");
	        return false;
	    } 
	}

	// Refresh token 검증
	public boolean isValidRefreshToken(String refreshToken) {
		return redisService.isValidRefreshToken(refreshToken);
	}
	
	// 요청 메세지에서 토큰 추출
	public String extractTokenFromRequest(HttpServletRequest httpServletRequest) {
		String authHeader = httpServletRequest.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring("Bearer ".length());
		}
		return null;
	}

	// 토큰에서 이메일 추출
	public String extractEmailFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(generateSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		return claims.getSubject();
	}
	
	public boolean isBlacklisted(String token) {
		return redisService.isBlacklisted(token);
	}
	
	
}
