package com.isdemirstaj.backend.dto.malzeme;

import com.isdemirstaj.backend.entity.enums.MenseiEnum;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class MalzemeCreateDto { // Malzeme entity'sinin oluşturulması için kullanılan DTO sınıfı

    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private MenseiEnum mensei;
    private String oper;

    
}
