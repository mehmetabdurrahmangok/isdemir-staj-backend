package com.isdemirstaj.backend.dto.malzeme;

import com.isdemirstaj.backend.entity.enums.MenseiEnum;
import lombok.Data;

@Data
public class MalzemeUpdateDto {
    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private MenseiEnum mensei;
    private String oper;
}