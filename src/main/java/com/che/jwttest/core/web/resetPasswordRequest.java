package com.che.jwttest.core.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class resetPasswordRequest {
	private String token;
	private String password;
	private String confirmPassword;
}
