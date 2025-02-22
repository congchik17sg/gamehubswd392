//package com.example.GameHub.configuration;
//
//
//import com.example.GameHub.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class ApplicationInitConfig {
//
//
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    ApplicationRunner applicationRunner(UserRepository userRepository) {
//        return args -> {
//            if (userRepository.findByUsername("admin").isEmpty()) {
//                var roles = new HashSet<String>();
//                roles.add(Role.Admin.name());
//                User user = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin"))
//                        .role(roles)
//                        .status(true)
//                        .build();
//
//                userRepository.save(user);
//                log.warn("admin user have been created");
//            }
//        };
//    }
//}
