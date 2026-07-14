package com.isdemirstaj.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequestDto {
    private String refreshToken;
}