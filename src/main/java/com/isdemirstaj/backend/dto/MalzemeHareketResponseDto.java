package com.isdemirstaj.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MalzemeHareketResponseDto {
    private Long MalzemeId;
    private LocalDateTime hareketTarihi;
    private double miktar;
    private String hareketTuru;
}
