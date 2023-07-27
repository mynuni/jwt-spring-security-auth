package com.study.auth.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickname;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public User(String email, String password, String nickname, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role == null ? Role.USER : role;
	}

}
