package com.example.GameHub.Controller;

import com.example.GameHub.model.request.RoleCreationRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.RoleResponse;
import com.example.GameHub.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
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
}
