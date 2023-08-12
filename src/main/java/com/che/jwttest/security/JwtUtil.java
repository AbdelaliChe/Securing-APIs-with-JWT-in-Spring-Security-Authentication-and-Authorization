package com.che.jwttest.security;

import com.che.jwttest.core.bo.userEntity;
import com.che.jwttest.core.dao.ItokenDao;
import com.che.jwttest.core.service.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil{

	@Autowired
	IUserService userService;

	@Autowired
	ItokenDao tokenDao;
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static final long EXPIRATION_TIME_MS = 900000;
	private static final long REFRESH_TOKEN_EXPIRATION_TIME_MS = 604800000;

	public String getUsernameFromToken(String token){
		return getClaimsFromToken(token,Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token){
		return getClaimsFromToken(token,Claims::getExpiration);
	}

	private  <T> T getClaimsFromToken(String token, Function<Claims,T> claimsResolver){
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
	}

	public Boolean isTokenExpired(String token){
		return getExpirationDateFromToken(token).before(new Date());
	}

	private static String createToken(Map<String, Object> claims, userEntity userDetails, long expirationTimeMs) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.claim("user_id", userDetails.getIdUser())
				.claim("user_role", userDetails.getAuthorities())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
				.signWith(SECRET_KEY)
				.compact();
	}

	public String generateToken(Map<String,Object> claims, userEntity userDetails){
		return createToken(claims, userDetails, EXPIRATION_TIME_MS);
	}

	public String generateRefreshToken(userEntity userDetails){
		return createToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRATION_TIME_MS);
	}

	public String generateToken(userEntity userDetails){
		return generateToken(new HashMap<>(),userDetails);
	}

	public Boolean validateToken(String token, UserDetails userDetails){
		return (!isTokenExpired(token) && getUsernameFromToken(token).equals(userDetails.getUsername()));
	}

}
