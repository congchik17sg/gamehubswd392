package com.example.GameHub.service;

import com.example.GameHub.entities.Role;
import com.example.GameHub.model.request.RoleCreationRequest;
import com.example.GameHub.model.response.RoleResponse;
import com.example.GameHub.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

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
}
