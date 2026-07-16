package com.isdemirstaj.backend.dto.malzemeHareket;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MalzemeHareketResponseDto {
    private Long id;
    private MalzemeResponseDto malzeme;
    private LocalDateTime hareketTarihi;
    private BigDecimal miktar;
    private String hareketTuru;
    private String oper;
    private LocalDateTime updatedAt;
}