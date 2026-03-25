package com.omnicharge.userservice.config;

import com.omnicharge.userservice.entity.User;
import com.omnicharge.userservice.enums.Role;
import com.omnicharge.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            String adminEmail = "admin@omnicharge.com";

            userRepository.findByEmail(adminEmail).ifPresentOrElse(
                    user -> {
                        user.setPassword(passwordEncoder.encode("Admin@123"));
                        user.setRole(Role.ADMIN);
                        userRepository.save(user);
                        System.out.println("[AdminSeeder] Admin user password updated successfully.");
                    },
                    () -> {
                        User admin = User.builder()
                                .name("Admin User")
                                .email(adminEmail)
                                .password(passwordEncoder.encode("Admin@123"))
                                .phone("9000000001")
                                .role(Role.ADMIN)
                                .build();
                        userRepository.save(admin);
                        System.out.println("[AdminSeeder] Admin user created successfully.");
                    }
            );


        };
    }
}
