package com.isdemirstaj.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String email;
    private String adSoyad;
    private String rol;
    private String accessToken;  // Arayüze yetkili istek atması için gönderilen token
    private String refreshToken; // Arayüze Access Token süresi bitince yenisini alması için verilen token
}