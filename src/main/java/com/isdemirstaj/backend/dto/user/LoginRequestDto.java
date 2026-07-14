package com.isdemirstaj.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String email; // Kullanıcı adı yerine e-posta ile giriş yapıyoruz
    private String password;
}