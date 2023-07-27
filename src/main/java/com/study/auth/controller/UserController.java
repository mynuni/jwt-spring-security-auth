package com.study.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.auth.dto.LoginRequestDto;
import com.study.auth.dto.SignUpRequestDto;
import com.study.auth.dto.SignUpResponseDto;
import com.study.auth.dto.TokenDto;
import com.study.auth.security.AuthenticationService;
import com.study.auth.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class UserController {
	private final UserService userService;
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		TokenDto tokenDto = authenticationService.authenticateUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
		return ResponseEntity.ok(tokenDto);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signupRequestDto) {
		SignUpResponseDto signUpResponseDto = userService.signUp(signupRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDto);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody TokenDto tokenDto) {
		log.info("ATK:{}", tokenDto.getAccessToken());
		log.info("RTK:{}", tokenDto.getRefreshToken());
		userService.logout(tokenDto);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	/**
	 * 	파라미터로 유저 가져올 때
	 *  @AuthenticationPrincipal이나 시큐리티 컨텍스트에서 가져와써야됨
	 */

}
