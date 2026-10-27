package com.ikaansh.ems.service;

import com.ikaansh.ems.dto.AuthResponse;
import com.ikaansh.ems.dto.LoginRequest;
import com.ikaansh.ems.dto.SignUpRequest;
import com.ikaansh.ems.entity.AppUser;
import com.ikaansh.ems.entity.UserRole;
import com.ikaansh.ems.exception.ConflictException;
import com.ikaansh.ems.exception.UnauthorizedException;
import com.ikaansh.ems.repository.UserRepository;
import com.ikaansh.ems.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }
    @Transactional public AuthResponse signUp(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ConflictException("An account with this email already exists.");
        AppUser user = users.save(new AppUser(request.fullName().trim(), email, encoder.encode(request.password()), UserRole.EMPLOYEE));
        return response(user);
    }
    @Transactional(readOnly = true) public AuthResponse login(LoginRequest request) {
        AppUser user = users.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
        if (!encoder.matches(request.password(), user.getPasswordHash())) throw new UnauthorizedException("Invalid email or password.");
        return response(user);
    }
    private AuthResponse response(AppUser user) { return new AuthResponse("Bearer", jwt.generateToken(user), jwt.getExpirationSeconds(), new AuthResponse.UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name())); }
}
