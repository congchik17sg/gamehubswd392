package com.example.GameHub.service;

import com.example.GameHub.entities.Role;
import com.example.GameHub.entities.User;
import com.example.GameHub.entities.UserRole;
import com.example.GameHub.model.request.RoleCreationRequest;
import com.example.GameHub.model.request.RoleUpdateRequest;
import com.example.GameHub.model.response.RoleResponse;
import com.example.GameHub.repository.RoleRepository;
import com.example.GameHub.repository.UserRepository;
import com.example.GameHub.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleResponse createRole(RoleCreationRequest request) {
        // Kiểm tra role đã tồn tại chưa
        roleRepository.findByRole(request.getRole())
                .ifPresent(r -> {
                    throw new RuntimeException("Role already exists: " + request.getRole());
                });

        // Tạo mới role
        Role role = Role.builder()
                .role(request.getRole())
                .create_at(LocalDate.now())
                .update_at(LocalDate.now())
                .build();

        Role savedRole = roleRepository.save(role);

        // Trả về response
        return RoleResponse.builder()
                .id(savedRole.getId())
                .role(savedRole.getRole())
                .create_at(savedRole.getCreate_at())
                .update_at(savedRole.getUpdate_at())
                .build();
    }
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .role(role.getRole())
                        .create_at(role.getCreate_at())
                        .update_at(role.getUpdate_at())
                        .build())
                .collect(Collectors.toList());
    }

    public RoleResponse updateRoleById(Long id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        role.setRole(request.getRole());
        role.setUpdate_at(LocalDate.now());

        Role updatedRole = roleRepository.save(role);

        return RoleResponse.builder()
                .id(updatedRole.getId())
                .role(updatedRole.getRole())
                .create_at(updatedRole.getCreate_at())
                .update_at(updatedRole.getUpdate_at())
                .build();
    }

    public void deleteRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        roleRepository.delete(role);
    }

    @Transactional
    public void addRoleByUserId(Long userId, Long roleId) {
        // Tìm user theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Tìm role theo ID
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Kiểm tra xem user đã có role này chưa
        boolean exists = userRoleRepository.existsByUserIdAndRoleId(userId, roleId);
        if (exists) {
            throw new RuntimeException("User already has this role!");
        }

        // Tạo UserRole mới
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .create_at(LocalDate.now())
                .update_at(LocalDate.now())
                .build();

        // Lưu vào DB
        userRoleRepository.save(userRole);
    }

}
