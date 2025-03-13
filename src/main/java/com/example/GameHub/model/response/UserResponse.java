package com.example.GameHub.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy") // ✅ Định dạng ngày tháng
    LocalDate create_at;

    @JsonFormat(pattern = "dd/MM/yyyy") // ✅ Định dạng ngày tháng
    LocalDate update_at;

    Set<String> roles;
}
