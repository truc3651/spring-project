package com.example.demo.security.otp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class OtpAuthentication implements Authentication {
  private final String email;
  private final String otp;
  private UserEntity user;
  private boolean isAuthenticated;

  public OtpAuthentication(String email, String otp) {
    this.email = email;
    this.otp = otp;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(user.getRole().name()));
  }

  @Override
  public Object getCredentials() {
    return otp;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return email;
  }

  @Override
  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.isAuthenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return email;
  }
}
