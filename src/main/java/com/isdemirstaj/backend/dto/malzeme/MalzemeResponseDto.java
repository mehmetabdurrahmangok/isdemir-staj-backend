package com.isdemirstaj.backend.dto.malzeme;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalzemeResponseDto { 
    private Long id;
    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private String malzemeTurAdi;
    private String mensei;
    private BigDecimal mevcutMiktar;
    private String oper;
    private LocalDateTime updatedAt;
}