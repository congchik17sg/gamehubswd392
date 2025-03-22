package com.example.GameHub.service;


import com.example.GameHub.entities.Role;
import com.example.GameHub.entities.User;
import com.example.GameHub.entities.UserRole;
import com.example.GameHub.exception.AppException;
import com.example.GameHub.exception.ErrorCode;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    @Autowired
    EmailService emailService;

    private static final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserResponse createUser(UserCreationRequest request, Set<String> roleNames) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        // ✅ Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)  // Lưu mật khẩu đã mã hóa
                .email(request.getEmail())
                .status(true)
                .create_at(LocalDate.now())
                .update_at(LocalDate.now())
                .build();

        userRepository.save(user);

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

        user.setStatus(false);
        userRepository.save(user);

        sendVerificationEmail(user);

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

    private void sendVerificationEmail(User user) {
        String verificationUrl = "http://localhost:8080/gamehub/auth/verify?email=" + user.getEmail();
        emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
    }

    public boolean verifyUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setStatus(true); // Đánh dấu là đã xác thực
        userRepository.save(user);
        return true;
    }

    //    public UserResponse getMyInfo() {
//        var context = SecurityContextHolder.getContext();
//        String name = context.getAuthentication().getName();
//
//        User user = userRepository.findByUsername(name)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        return convertToUserResponse(user); // ✅ Dùng hàm tự viết
//    }
    public UserResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new RuntimeException("User not authenticated");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getClaim("username");

        return userRepository.findByUsername(username)
                .map(user -> UserResponse.builder()
                        .username(user.getUsername())
                        .password(user.getPassword()) // ⚠️ Cân nhắc bỏ trường password trong response để bảo mật
                        .email(user.getEmail())
                        .create_at(user.getCreate_at())
                        .update_at(user.getUpdate_at())
                        .roles(user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getRole()) // Lấy danh sách role
                                .collect(Collectors.toSet()))
                        .build())
                .orElseThrow(() -> new RuntimeException("User Not Existed"));
    }


    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .create_at(user.getCreate_at())
                .update_at(user.getUpdate_at())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getRole())
                        .collect(Collectors.toSet()))
                .build();
    }

    public List<UserResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return convertToUserResponse(user);
    }

    public UserResponse updateUserById(Long userId, UserCreationRequest request, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUpdate_at(LocalDate.now());

        // Cập nhật danh sách vai trò nếu có thay đổi
        if (roleNames != null && !roleNames.isEmpty()) {
            Set<UserRole> userRoles = roleNames.stream()
                    .map(roleName -> {
                        Role role = roleRepository.findByRole(roleName)
                                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                        return UserRole.builder()
                                .user(user)
                                .role(role)
                                .create_at(LocalDate.now())
                                .update_at(LocalDate.now())
                                .build();
                    }).collect(Collectors.toSet());
            user.setUserRoles(userRoles);
        }

        userRepository.save(user);
        return convertToUserResponse(user);
    }

    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.delete(user);
    }


}
