package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequest {
  private String sessionId;
  private String email; // can be email, username, phone number
  private String password;
}
