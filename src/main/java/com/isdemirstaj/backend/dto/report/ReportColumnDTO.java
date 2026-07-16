package com.isdemirstaj.backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// bu sınıf rapordaki her bir sütunu temsil ediyor

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportColumnDTO {
    private String headerName; // Sütun başlığı
    private boolean isTotalColumn; // Bu sütun bir toplam sütunu mu?
    private String malzemeTurAdi; // sütunun hangi tür için olduğu
}
