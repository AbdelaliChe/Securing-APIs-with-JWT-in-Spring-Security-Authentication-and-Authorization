package com.che.jwttest.security;

import com.che.jwttest.core.bo.tokenEntity;
import com.che.jwttest.core.dao.ItokenDao;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ItokenDao tokenDao;
	@Autowired
	private MyUserDetailsService userDetailsService;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");
		final String jwtToken;
		final String username;

		if(authorizationHeader!= null && authorizationHeader.startsWith("Bearer ")){
			jwtToken = authorizationHeader.substring(7);
			tokenEntity token = tokenDao.findByToken(jwtToken);
			try{
				handleJwt(request, jwtToken, token);
			}catch (IllegalArgumentException e){
				logger.error("unable to fetch jwt token");
			}catch (ExpiredJwtException e){
				logger.error("jwt token expired");
				handleExpiredJwt(token);
			}catch (Exception e){
				logger.error(e.getMessage());
			}

		}else{
			logger.warn("jwt token doesnt begun with bearer");
		}
		filterChain.doFilter(request,response);
	}

	private void handleExpiredJwt(tokenEntity token) {
		if(token.isExpired() && token.isRevoked()) return;
		token.setExpired(true);
		token.setRevoked(true);
		tokenDao.save(token);
	}

	private void handleJwt(HttpServletRequest request, String jwtToken, tokenEntity token) {
		final String username;
		username = jwtUtil.getUsernameFromToken(jwtToken);
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if(jwtUtil.validateToken(jwtToken, userDetails) && !token.isExpired() && !token.isRevoked()){
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
	}
}
