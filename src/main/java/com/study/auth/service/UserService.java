package com.study.auth.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.auth.domain.Role;
import com.study.auth.domain.User;
import com.study.auth.dto.SignUpRequestDto;
import com.study.auth.dto.SignUpResponseDto;
import com.study.auth.dto.TokenDto;
import com.study.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final RedisService redisService;

	@Transactional
	public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
		User user = modelMapper.map(signUpRequestDto, User.class);
		user.setRole(Role.USER);
		User savedUser = userRepository.save(user);

		return modelMapper.map(savedUser, SignUpResponseDto.class);
	}

	public void logout(TokenDto tokenDto) {
		String accessToken = tokenDto.getAccessToken();
		String refreshToken = tokenDto.getRefreshToken();

		if (accessToken != null) {
			redisService.addToBlacklist(accessToken);
		}

		if (refreshToken != null) {
			redisService.deleteRefreshToken(refreshToken);
		}

	}

}
