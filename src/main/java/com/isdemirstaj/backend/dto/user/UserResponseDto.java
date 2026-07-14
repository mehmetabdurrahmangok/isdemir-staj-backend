package com.isdemirstaj.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String adSoyad;
    private String rol;
    private Boolean aktifMi;
}