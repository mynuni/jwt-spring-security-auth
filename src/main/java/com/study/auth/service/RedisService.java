package com.study.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisService {
	
	private final RedisTemplate<String,String> redisTemplate;
	private final String REFRESH_PREFIX = "REFRESH:";
	private final String BLACKLIST_PREFIX = "BLACKLIST:";
	private final String BLACKLIST_VALUE = "BLACKLISTED";
	private final long REFRESH_EXP_TIME = 180;
	private final long BLACKLIST_EXP_TIME = 180;
	
	public void addRefreshToken(String token, String email) {
		String key = REFRESH_PREFIX + token;
		redisTemplate.opsForValue().set(key, email, REFRESH_EXP_TIME, TimeUnit.SECONDS);
	}

	public void deleteRefreshToken(String token) {
		String key = REFRESH_PREFIX + token;
		redisTemplate.delete(key);
	}
	
	public boolean isValidRefreshToken(String token) {
        String key = REFRESH_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
	
	public void addToBlacklist(String token) {
		String key = BLACKLIST_PREFIX + token;
		redisTemplate.opsForValue().set(key, BLACKLIST_VALUE, BLACKLIST_EXP_TIME, TimeUnit.SECONDS);
	}
	
	public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
