package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterRequest {
  private String fullName; // can be email, username, phone number
  private String email;
  private String password;
}
