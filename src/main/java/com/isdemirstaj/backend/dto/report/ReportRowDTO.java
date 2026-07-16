package com.isdemirstaj.backend.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// Bu sınıf rapordaki her bir gün için yapı vericek

@Data
public class ReportRowDTO {
    private String tarih; // her bir satır için tarih bilgisi

    private Map<String, BigDecimal> values = new LinkedHashMap<>();
}

