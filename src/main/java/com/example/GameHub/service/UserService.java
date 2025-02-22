package com.example.GameHub.service;


import com.example.GameHub.entities.Role;
import com.example.GameHub.entities.User;
import com.example.GameHub.entities.UserRole;
import com.example.GameHub.model.request.UserCreationRequest;
import com.example.GameHub.model.response.UserResponse;
import com.example.GameHub.repository.RoleRepository;
import com.example.GameHub.repository.UserRepository;
import com.example.GameHub.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    public UserResponse createUser(UserCreationRequest request, Set<String> roleNames) {
        // 1. Tạo đối tượng User từ request
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // Cân nhắc mã hóa mật khẩu
                .email(request.getEmail())
                .status(true)
                .create_at(LocalDate.now())
                .update_at(LocalDate.now())
                .build();

        // 2. Lưu user trước để sinh ID
        userRepository.save(user);

        // 3. Tạo UserRole từ roleNames
        Set<UserRole> userRoles = roleNames.stream()
                .map(roleName -> {
                    Role role = roleRepository.findByRole(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    return UserRole.builder()
                            .user(user)
                            .role(role)
                            .create_at(LocalDate.now())
                            .update_at(LocalDate.now())
                            .build();
                }).collect(Collectors.toSet());

        user.setUserRoles(userRoles);
        userRoleRepository.saveAll(userRoles);

        // 4. Trả về UserResponse
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .create_at(user.getCreate_at())
                .update_at(user.getUpdate_at())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getRole())
                        .collect(Collectors.toSet()))
                .build();

    }


}
