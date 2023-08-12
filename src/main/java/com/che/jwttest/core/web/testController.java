package com.che.jwttest.core.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class testController {
	@GetMapping("/admin")
	public ResponseEntity<String> homeAdmin(){
		return ResponseEntity.ok("hello admin");
	}

	@GetMapping("/user")
	public ResponseEntity<String> homeUser(){
		return ResponseEntity.ok("hello user");
	}
}
