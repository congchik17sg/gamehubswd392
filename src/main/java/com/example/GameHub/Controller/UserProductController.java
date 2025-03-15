package com.example.GameHub.Controller;

import com.example.GameHub.model.request.UserProductRequest;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.service.UserProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-products")
@RequiredArgsConstructor
public class UserProductController {
    private final UserProductService userProductService;

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addOwnership(@RequestBody @Valid UserProductRequest request) {
        return userProductService.addOwnership(request);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getUserProducts(@PathVariable Long userId) {
        return userProductService.getUserProducts(userId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteOwnership(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        return userProductService.deleteOwnership(userId, productId);
    }

}
