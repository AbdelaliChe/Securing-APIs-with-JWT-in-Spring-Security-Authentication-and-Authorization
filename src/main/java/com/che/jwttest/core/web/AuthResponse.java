package com.che.jwttest.core.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {

	private String accessToken;
	private String refreshToken;
}
