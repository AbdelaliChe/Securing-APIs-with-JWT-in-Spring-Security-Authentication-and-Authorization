package com.che.jwttest.core.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterRequest {
	private Long idUser;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

}
