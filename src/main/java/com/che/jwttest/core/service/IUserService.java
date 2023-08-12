package com.che.jwttest.core.service;

import com.che.jwttest.core.bo.userEntity;
import com.che.jwttest.core.web.AutRequest;
import com.che.jwttest.core.web.AuthResponse;
import com.che.jwttest.core.web.RegisterRequest;
import com.che.jwttest.core.web.resetPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IUserService {
	userEntity getUserByEmail(String email);
	List<userEntity> getUsers();
	AuthResponse register(RegisterRequest request);

	AuthResponse authenticate(AutRequest request);

	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

	String generatePasswordResetToken(userEntity user);


	Map<String, String> resetPassword(resetPasswordRequest request);
}
