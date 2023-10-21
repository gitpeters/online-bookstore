package com.peters.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String access_token;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isEnabled;
}
