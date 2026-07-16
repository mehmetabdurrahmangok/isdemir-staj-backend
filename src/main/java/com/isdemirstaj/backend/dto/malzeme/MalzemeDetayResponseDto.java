package com.isdemirstaj.backend.dto.malzeme;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalzemeDetayResponseDto {
    private Long id;
    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private String malzemeTurAdi;
    private String mensei;
    private BigDecimal mevcutMiktar; // Bir önceki görevde hesapladığımız net stok
    private String oper;
    private java.time.LocalDateTime updatedAt;
    private List<HareketDetayDto> hareketler; // Geçmiş hareketlerin listesi

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HareketDetayDto {
        private Long id;
        private java.time.LocalDateTime hareketTarihi;
        private BigDecimal miktar;
        private String hareketTuru;
        private String oper;
        private java.time.LocalDateTime updatedAt;
    }
}