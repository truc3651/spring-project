package com.example.demo.entities;

import com.example.demo.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_users")
public class UserEntity {
  @Id
  // jpa pick appropriate solution for any driver
  // postgres: sequence
  // mysql: table
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String fullName;
  private String email;
  private String password;
  private String otpSecret;

  @Enumerated
  private Role role;
}
