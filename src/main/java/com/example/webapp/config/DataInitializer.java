package com.example.webapp.config;

import com.example.webapp.model.User;
import com.example.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) {
        userRepository.save(new User("admin", "admin123", "admin@example.com", "admin"));
        userRepository.save(new User("user", "user123", "user@example.com", "user"));
    }
}