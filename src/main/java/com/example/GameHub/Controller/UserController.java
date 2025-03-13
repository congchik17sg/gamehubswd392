package com.example.GameHub.Controller;

import com.example.GameHub.model.request.UserCreationRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.UserResponse;
import com.example.GameHub.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController // ✅ Đổi từ @Controller thành @RestController để trả về JSON
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = "API user")
public class UserController {

    private final UserService userService;

    // 1. Lấy danh sách tất cả người dùng
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // 2. Lấy thông tin một user theo ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // 3. Tạo mới user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request,
                                                   @RequestParam Set<String> roleNames) {
        UserResponse user = userService.createUser(request, roleNames);
        return ResponseEntity.ok(user);
    }

    // 4. Cập nhật thông tin user theo ID
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long userId,
                                                       @RequestBody UserCreationRequest request,
                                                       @RequestParam Set<String> roleNames) {
        UserResponse updatedUser = userService.updateUserById(userId, request, roleNames);
        return ResponseEntity.ok(updatedUser);
    }

    // 5. Xóa user theo ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(1000)
                .message("User deleted successfully")
                .result(null) // Vì không có dữ liệu trả về
                .build());
    }

}
