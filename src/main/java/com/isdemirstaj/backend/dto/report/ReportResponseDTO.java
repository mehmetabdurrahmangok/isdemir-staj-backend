package com.isdemirstaj.backend.dto.report;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// genel response yapısı

@Data
public class ReportResponseDTO {
    private List<ReportColumnDTO> columns; // Tablonun dinamik sütunları
    private List<ReportRowDTO> rows;       // Tablonun satırları (tarih bazlı)
    private Map<String, BigDecimal> rowTotal; // En alt satırdaki "GENEL TOPLAM" verileri
}