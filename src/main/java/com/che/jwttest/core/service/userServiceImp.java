package com.che.jwttest.core.service;

import com.che.jwttest.core.bo.Role;
import com.che.jwttest.core.bo.tokenEntity;
import com.che.jwttest.core.bo.userEntity;
import com.che.jwttest.core.dao.IUserDao;
import com.che.jwttest.core.dao.ItokenDao;
import com.che.jwttest.core.web.AutRequest;
import com.che.jwttest.core.web.AuthResponse;
import com.che.jwttest.core.web.RegisterRequest;
import com.che.jwttest.core.web.resetPasswordRequest;
import com.che.jwttest.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
@Service
public class userServiceImp implements IUserService{

	@Autowired
	IUserDao userDao;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	ItokenDao tokenDao;

	@Autowired
	AuthenticationManager authenticationManager;

	@Override
	public userEntity getUserByEmail(String email) {
		return userDao.findByEmail(email);
	}


	@Override
	public List<userEntity> getUsers() {
		return userDao.findAll();
	}


	private void revokeUserToken(userEntity user){
		Set<tokenEntity> valideTokens = tokenDao.findValidTokenByUser(user.getIdUser());
		if(valideTokens.isEmpty()){
			return;
		}
		valideTokens.forEach(token->{
			token.setRevoked(true);
			token.setExpired(true);
		});
		tokenDao.saveAll(valideTokens);
	}

	private void saveUserToken(userEntity user, String jwtToken) {

		tokenEntity tokenEntity = new tokenEntity();
		tokenEntity.setToken(jwtToken);
		tokenEntity.setType("BEARER");
		tokenEntity.setExpired(false);
		tokenEntity.setRevoked(false);
		tokenEntity.setUser(user);
		tokenDao.save(tokenEntity);
	}

	private userEntity createUser(RegisterRequest request) {
		userEntity user = new userEntity();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		String encryptedPassword = passwordEncoder.encode(request.getPassword());
		user.setPassword(encryptedPassword);
		user.setRole(Role.USER);
		return user;
	}

	@Override
	public AuthResponse register(RegisterRequest request) {

		userEntity user = createUser(request);
		try{
			userEntity newUser = userDao.save(user);
			String jwtToken = jwtUtil.generateToken(user);
			String refreshToken = jwtUtil.generateRefreshToken(user);

			revokeUserToken(user);
			saveUserToken(newUser, jwtToken);

			return new AuthResponse(jwtToken,refreshToken);
		}catch (DataIntegrityViolationException e){
			throw new DuplicateEmailException("Email "+request.getEmail()+" already exists!");
		}
	}

	@Override
	public AuthResponse authenticate(AutRequest request) {

		try{
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),request.getPassword()
					)
			);
			userEntity user = userDao.findByEmail(request.getEmail());
			String jwtToken = jwtUtil.generateToken(user);
			String refreshToken = jwtUtil.generateRefreshToken(user);

			revokeUserToken(user);
			saveUserToken(user,jwtToken);

			return new AuthResponse(jwtToken,refreshToken);
		}catch (BadCredentialsException e){
			throw new BadCredentialsException("Invalid email or password!");
		}
	}

	@Override
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

		final String authorizationHeader = request.getHeader("Authorization");
		final String refreshToken;
		final String username;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			refreshToken = authorizationHeader.substring(7);

			username = jwtUtil.getUsernameFromToken(refreshToken);
			if (username != null) {

				userEntity user = userDao.findByEmail(username);
				if (jwtUtil.validateToken(refreshToken, user)) {
					String newToken = jwtUtil.generateToken(user);
					revokeUserToken(user);
					saveUserToken(user,newToken);

					AuthResponse authResponse= new AuthResponse();
					authResponse.setAccessToken(newToken);
					authResponse.setRefreshToken(refreshToken);

					response.setContentType("application/json");
					new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
				}
			}

		}
	}

	@Override
	public String generatePasswordResetToken(userEntity user) {

		String jwtToken = jwtUtil.generateToken(user);

		revokeUserToken(user);
		saveUserToken(user,jwtToken);

		return jwtToken;
	}

	@Override
	public Map<String, String> resetPassword(resetPasswordRequest request) {
		Map<String, String> response = new HashMap<>();
		String email = jwtUtil.getUsernameFromToken(request.getToken());
		if (email != null) {
			userEntity user = userDao.findByEmail(email);
			if(jwtUtil.validateToken(request.getToken(), user)
					&& !tokenDao.findByToken(request.getToken()).isExpired()
					&& !tokenDao.findByToken(request.getToken()).isRevoked()){
					String encryptedPassword = passwordEncoder.encode(request.getPassword());
					user.setPassword(encryptedPassword);
					revokeUserToken(user);
					userDao.save(user);
					response.put("message", "Password successfully updated");
				return response;
			}else {
				response.put("message", "Invalid token");
				return response;
			}
		}
		response.put("message", "No access");
		return response;
	}

}
