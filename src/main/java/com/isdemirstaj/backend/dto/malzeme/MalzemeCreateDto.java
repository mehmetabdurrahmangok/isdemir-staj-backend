package com.isdemirstaj.backend.dto.malzeme;

import com.isdemirstaj.backend.entity.enums.MenseiEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class MalzemeCreateDto { // Malzeme entity'sinin oluşturulması için kullanılan DTO sınıfı

    private String malzemeKodu;

    @NotBlank(message = "Malzeme adı boş bırakılamaz!!!")
    @Size(min = 2, max = 50, message = "Malzeme adının uzunluğu 2 ve 50 karakter arasında olmalıdır.")
    // Koruma kısmı
    @Pattern(regexp = "^[a-zA-ZçğıöşüÇĞİÖŞÜ\\s]+$", message = "Sadece Türkçe karakterler kullanılabilir!!")
    private String malzemeAdi;
    private Long malzemeTurId;
    private MenseiEnum mensei;
    private String oper;

    
}
