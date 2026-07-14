package com.isdemirstaj.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String email;
    private String password;
    private String adSoyad;
    private String oper; // BaseEntity logunu doldurmak için (Örn: "SYSTEM")
}