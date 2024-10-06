package com.example.demo.security.otp;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
//@RequiredArgsConstructor
public class OtpAuthProvider implements AuthenticationProvider {
  private final UserRepository userRepository;
  private final OtpService otpService;

  public OtpAuthProvider(UserRepository userRepository, OtpService otpService) {
    this.userRepository = userRepository;
    this.otpService = otpService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    OtpAuthentication otpAuth = (OtpAuthentication) authentication;

    String email = otpAuth.getEmail();
    String otp = otpAuth.getOtp();
    UserEntity user = userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

    if(!otpService.isOtpValid(user.getOtpSecret(), otp)) {
      throw new BadCredentialsException("Otp invalid");
    }

    OtpAuthentication authenticated = new OtpAuthentication(email, otp);
    authenticated.setAuthenticated(true);
    authenticated.setUser(user);
    return authenticated;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return OtpAuthentication.class.equals(authentication);
  }
}
