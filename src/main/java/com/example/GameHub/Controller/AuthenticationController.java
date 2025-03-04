package com.example.GameHub.Controller;


import com.example.GameHub.model.request.AuthenticationRequest;
import com.example.GameHub.model.request.UserCreationRequest;
import com.example.GameHub.model.response.ApiResponse;
import com.example.GameHub.model.response.AuthenticationResponse;
import com.example.GameHub.model.response.UserResponse;
import com.example.GameHub.service.AuthenticationService;
import com.example.GameHub.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication API", description = "API authen and author")
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;


    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request, request.getRoleNames())) // Truyền roleNames
                .build();
    }

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticationResponse(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

//    @PostMapping("/introspect")
//    ApiResponse<IntrospectResponse> authenticationResponse(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
//        var result = authenticationService.introspect(request);
//        return ApiResponse.<IntrospectResponse>builder()
//                .result(result)
//                .build();
//    }


    @GetMapping("/verify")
    @Operation(summary = "Xác thực tài khoản email")
    public ModelAndView verifyUser(@RequestParam String email) {
        boolean verified = userService.verifyUser(email);

        ModelAndView modelAndView = new ModelAndView("verification-result");
        modelAndView.addObject("email", email);
        modelAndView.addObject("status", verified ? "success" : "error");

        return modelAndView;
    }
}
