package com.isdemirstaj.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
    private String refreshToken;
}