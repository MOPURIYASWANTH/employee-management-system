package com.ikaansh.ems.dto;

public record AuthResponse(String tokenType, String accessToken, long expiresInSeconds, UserResponse user) {
	public record UserResponse(Long id, String fullName, String email, String role) {
	}
}
