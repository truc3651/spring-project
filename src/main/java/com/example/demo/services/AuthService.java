package com.example.demo.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.LoginOtpRequest;
import com.example.demo.dtos.LoginOtpResponse;
import com.example.demo.entities.SessionEntity;
import com.example.demo.repositories.SessionRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.SecurityUser;
import com.example.demo.dtos.LoginResponse;
import com.example.demo.dtos.LoginRequest;
import com.example.demo.dtos.RegisterRequest;
import com.example.demo.dtos.RegisterResponse;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.otp.OtpAuthentication;
import com.example.demo.security.otp.OtpService;

import exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final OtpService otpService;
  private final AuthenticationManager authenticationManager;

  public RegisterResponse register(RegisterRequest registerRequest) {
    String otpSecret = otpService.generateOtpSecret();

    UserEntity user = UserEntity.builder()
        .fullName(registerRequest.getFullName())
        .email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .role(Role.CUSTOMER)
        .otpSecret(otpSecret)
        .build();
    userRepository.save(user);

    return new RegisterResponse(otpSecret);
  }

  public void login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
    );
//    UserEntity user = userRepository
//        .findByEmail(loginRequest.getEmail())
//        .orElseThrow();
//    String accessToken = jwtService.generateToken(new SecurityUser(user));
//    return new LoginResponse(accessToken);
    SessionEntity session = SessionEntity
        .builder()
        .id(loginRequest.getSessionId())
        .email(loginRequest.getEmail())
        .expiredAt(OffsetDateTime.now().plusMinutes(10))
        .build();
    sessionRepository.save(session);
  }

  public LoginOtpResponse loginWithOtp(LoginOtpRequest otpLoginRequest) {
//    authenticationManager.authenticate(
//        new OtpAuthentication(otpLoginRequest.getEmail(), otpLoginRequest.getOtp())
//    );
//    UserEntity user = userRepository
//        .findByEmail(otpLoginRequest.getEmail())
//        .orElseThrow();
//    String accessToken = jwtService.generateToken(new SecurityUser(user));
//    return new LoginResponse(accessToken);

    SessionEntity session = sessionRepository
        .findById(otpLoginRequest.getSessionId())
        .orElseThrow(() -> new BadRequestException("Session invalid"));

    if(!session.getEmail().equals(otpLoginRequest.getEmail())) {
      throw new BadRequestException("Session and email is mismatch");
    }

    if (session.getExpiredAt().isBefore(OffsetDateTime.now())) {
      throw new BadRequestException("Session expired");
    }

    authenticationManager.authenticate(
      new OtpAuthentication(otpLoginRequest.getEmail(), otpLoginRequest.getOtp())
    );
    UserEntity user = userRepository
        .findByEmail(otpLoginRequest.getEmail())
        .orElseThrow();
    String accessToken = jwtService.generateToken(new SecurityUser(user));
    return new LoginOtpResponse(accessToken);
  }
}
