package com.example.GameHub.configuration;

import com.example.GameHub.entities.Role;
import com.example.GameHub.entities.User;
import com.example.GameHub.entities.UserRole;
import com.example.GameHub.repository.RoleRepository;
import com.example.GameHub.repository.UserRepository;
import com.example.GameHub.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

//    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        UserRoleRepository userRoleRepository) {
        return args -> {
            // Kiểm tra xem role ADMIN đã tồn tại chưa
            Optional<Role> adminRoleOpt = roleRepository.findByRole("ADMIN");
            Role adminRole;
            if (adminRoleOpt.isEmpty()) {
                adminRole = Role.builder()
                        .role("ADMIN")
                        .create_at(LocalDate.now())
                        .update_at(LocalDate.now())
                        .build();
                roleRepository.save(adminRole);
                log.info("Role ADMIN has been created");
            } else {
                adminRole = adminRoleOpt.get();
            }

            // Kiểm tra xem user admin đã tồn tại chưa
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = User.builder()
                        .username("admin")
                        .password(("admin"))
                        .email("admin@example.com")
                        .status(true)
                        .create_at(LocalDate.now())
                        .update_at(LocalDate.now())
                        .build();
                userRepository.save(adminUser);

                // Gán quyền ADMIN cho user admin
                UserRole userRole = UserRole.builder()
                        .user(adminUser)
                        .role(adminRole)
                        .create_at(LocalDate.now())
                        .update_at(LocalDate.now())
                        .build();
                userRoleRepository.save(userRole);
                log.info("Admin user has been created and assigned ADMIN role");
            }
        };
    }
}