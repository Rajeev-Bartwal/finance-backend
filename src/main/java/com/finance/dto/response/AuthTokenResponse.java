package com.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenResponse {
    private String token;
    private String tokenType;
    private UserResponse user;

    public static AuthTokenResponse of(String token, UserResponse user) {
        return new AuthTokenResponse(token, "Bearer", user);
    }
}
