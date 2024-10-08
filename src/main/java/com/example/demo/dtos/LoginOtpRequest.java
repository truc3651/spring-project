package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginOtpRequest {
  private String sessionId;
  private String email;
  private String otp;
}
