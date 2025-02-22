package com.example.GameHub.Controller;

import com.example.GameHub.model.request.UserCreationRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.UserResponse;
import com.example.GameHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @ResponseBody // Để trả JSON từ @Controller
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request,
                                                @RequestParam Set<String> roles) {
        UserResponse userResponse = userService.createUser(request, roles);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User created successfully.")
                .data(userResponse)
                .build();
    }

}
