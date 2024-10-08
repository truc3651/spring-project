package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.SessionEntity;
import com.example.demo.entities.UserEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {
}
