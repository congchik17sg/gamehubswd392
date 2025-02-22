package com.example.GameHub.model.response;

import com.example.GameHub.entities.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String username;
    String password;
    String email;
    LocalDate create_at;
    LocalDate update_at;

    Set<String> roles;

}
