package com.isdemirstaj.backend.dto.malzemeTur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MalzemeTurCreateDto {
    @NotBlank(message = "Malzeme türü ismi boş bırakılamaz!!")
    @Size(min = 2, max = 20, message = "malzeme tür ismi 2 ve 20 karakter arasında olmalı!!!")
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "Sadece Türkçe karakterler kullanılabilir!!")
    private String malzemeTurAdi;
    private String oper;
}