package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.LoginResponse;
import com.example.demo.dtos.LoginRequest;
import com.example.demo.dtos.OtpLoginRequest;
import com.example.demo.dtos.RegisterRequest;
import com.example.demo.dtos.RegisterResponse;
import com.example.demo.services.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<RegisterResponse> register(
      @RequestBody RegisterRequest registerRequest
  ) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  @PostMapping("/login")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<LoginResponse> login(
      @RequestBody LoginRequest loginRequest
  ) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @PostMapping("/login/otp")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<LoginResponse> loginWithOtp(
      @RequestBody OtpLoginRequest otpLoginRequest
  ) {
    return ResponseEntity.ok(authService.loginWithOtp(otpLoginRequest));
  }
}
