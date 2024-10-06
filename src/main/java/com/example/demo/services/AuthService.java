package com.example.demo.services;

import static com.eatthepath.otp.TimeBasedOneTimePasswordGenerator.DEFAULT_TIME_STEP;
import static com.eatthepath.otp.TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.example.demo.dtos.OtpLoginRequest;
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

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
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

  public LoginResponse login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
    );
    UserEntity user = userRepository
        .findByEmail(loginRequest.getEmail())
        .orElseThrow();
    String accessToken = jwtService.generateToken(new SecurityUser(user));
    return new LoginResponse(accessToken);
  }

  public LoginResponse loginWithOtp(OtpLoginRequest otpLoginRequest) {
    authenticationManager.authenticate(
        new OtpAuthentication(otpLoginRequest.getEmail(), otpLoginRequest.getOtp())
    );
    UserEntity user = userRepository
        .findByEmail(otpLoginRequest.getEmail())
        .orElseThrow();
    String accessToken = jwtService.generateToken(new SecurityUser(user));
    return new LoginResponse(accessToken);

  }
}
