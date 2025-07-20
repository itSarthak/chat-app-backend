package com.chatApp.backend.ChatAppBackend.repository;

import com.chatApp.backend.ChatAppBackend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
