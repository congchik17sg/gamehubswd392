package com.example.GameHub.Controller;

import com.example.GameHub.model.request.RoleCreationRequest;
import com.example.GameHub.model.request.RoleUpdateRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.RoleResponse;
import com.example.GameHub.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
@Tag(name = "Role", description = "API role")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    @ResponseBody
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleCreationRequest request) {
        RoleResponse roleResponse = roleService.createRole(request);
        return ApiResponse.<RoleResponse>builder()
                .result(roleResponse)
                .message("Role created successfully.")
                .build();
    }
    @GetMapping("/all")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roles)
                .message("Roles fetched successfully.")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRoleById(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        RoleResponse updatedRole = roleService.updateRoleById(id, request);
        return ApiResponse.<RoleResponse>builder()
                .result(updatedRole)
                .message("Role updated successfully.")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ApiResponse.<Void>builder()
                .message("Role deleted successfully.")
                .build();
    }
    @PostMapping("/addRole")
    public ApiResponse<String> addRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        roleService.addRoleByUserId(userId, roleId);
        return ApiResponse.<String>builder()
                .result("Role added successfully to user")
                .message("Success")
                .build();
    }
}
