package com.che.jwttest.core.web;

import com.che.jwttest.core.bo.userEntity;
import com.che.jwttest.core.service.DuplicateEmailException;
import com.che.jwttest.core.service.IUserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	IUserService userService;

	@Autowired
	JavaMailSender mailSender;


	@GetMapping("/hello")
	public ResponseEntity<String> home(){
		return ResponseEntity.ok("hello");
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request){
		try{
			return ResponseEntity.ok(userService.register(request));
		}catch (DuplicateEmailException e){
			Map<String, String> response = new HashMap<>();
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AutRequest request){
		try{
			return ResponseEntity.ok(userService.authenticate(request));
		}catch (BadCredentialsException e){
			Map<String, String> response = new HashMap<>();
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/refreshToken")
	public void refreshToken(
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException {
		userService.refreshToken(request,response);

	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
		userEntity user = userService.getUserByEmail(email);
		Map<String, String> response = new HashMap<>();

		if(user!=null){
			String jwtToken = userService.generatePasswordResetToken(user);

			String resetLink = "http://localhost:8080/api/auth/resetPassword?token=" + jwtToken;


			String emailContent = createEmailContent(user, resetLink);

			sendEmail(email, emailContent);

			response.put("message", "check ur email!");
			return ResponseEntity.ok(response);
		}
		response.put("message", "email doesn't exist!");
		return ResponseEntity.badRequest().body(response);
	}

	private void sendEmail(String email, String emailContent) throws MessagingException {

		MimeMessage mailMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
		messageHelper.setFrom("cxea.abdelali@gmail.com");
		messageHelper.setSubject("Password Reset Mail for Spring");
		messageHelper.setText(emailContent,true);
		messageHelper.setTo(email);

		mailSender.send(mailMessage);
	}

	private static String createEmailContent(userEntity user, String resetLink) {

		String emailContent = "<p>Hi " + user.getLastName()+" "+ user.getFirstName()+"</p>"+
				"<p>We received a request to reset your password." +
				" If you did not initiate this request, please ignore this email</p>" +
				"<p>To reset your password, click on the link below:</p> <a href=\"" +
				resetLink + "\"> Reset Password</a>" +
				" this link is available just for 15min"+
				"<p>Thank you,<br> Email test Team</p>";
		return emailContent;

	}

	@PutMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody resetPasswordRequest request){
		Map<String,String> response = userService.resetPassword(request);
		HttpStatus status = response.get("message").equals("Password successfully updated")?HttpStatus.OK:HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(response);
	}

}
