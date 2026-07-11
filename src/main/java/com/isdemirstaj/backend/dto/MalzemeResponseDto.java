package com.isdemirstaj.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalzemeResponseDto { // Malzeme entity'sinin DTO sınıfı
    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private String malzemeTurAdi;
    private String mensei;
    private LocalDateTime updatedAt;
}
