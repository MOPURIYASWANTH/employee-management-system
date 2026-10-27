package com.ikaansh.ems.controller;

import com.ikaansh.ems.dto.AuthResponse;
import com.ikaansh.ems.dto.LoginRequest;
import com.ikaansh.ems.dto.SignUpRequest;
import com.ikaansh.ems.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse signUp(@Valid @RequestBody SignUpRequest request) {
		return authService.signUp(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
