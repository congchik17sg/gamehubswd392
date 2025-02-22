package com.example.GameHub.Controller;

import com.example.GameHub.model.request.RoleCreationRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.RoleResponse;
import com.example.GameHub.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    @ResponseBody
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleCreationRequest request) {
        RoleResponse roleResponse = roleService.createRole(request);
        return ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Role created successfully.")
                .data(roleResponse)
                .build();
    }
}
