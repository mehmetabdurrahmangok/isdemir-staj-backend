package com.isdemirstaj.backend.dto.malzemeHareket;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class MalzemeHareketUpdateDto {
    private Long malzemeId;
    private LocalDateTime hareketTarihi;
    private BigDecimal miktar;
    private String hareketTuru;
    private String oper;
}