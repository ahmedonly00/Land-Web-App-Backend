package com.iwacu250.landplots.dto.auth;

import com.iwacu250.landplots.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}
