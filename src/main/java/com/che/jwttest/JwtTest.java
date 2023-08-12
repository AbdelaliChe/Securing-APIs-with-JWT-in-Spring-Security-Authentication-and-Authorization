package com.che.jwttest;

import com.che.jwttest.core.bo.Role;
import com.che.jwttest.core.bo.userEntity;
import com.che.jwttest.core.dao.IUserDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JwtTest {

	public static void main(String[] args) {
		SpringApplication.run(JwtTest.class, args);
	}

	@Bean
	CommandLineRunner run(IUserDao userDao, PasswordEncoder passwordEncode){
		return args ->{
			if(userDao.findByRole(Role.ADMIN)!=null) return;

			userEntity admin = new userEntity();

			admin.setFirstName("admin");
			admin.setLastName("admin");
			admin.setEmail("admin@admin.com");
			admin.setPassword(passwordEncode.encode("password"));
			admin.setRole(Role.ADMIN);

			userDao.save(admin);
		};
	}

}
