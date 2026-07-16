package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.report.ReportResponseDTO;
import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;
import com.isdemirstaj.backend.service.ExcelExportService;
import com.isdemirstaj.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Eğer frontend'den CORS hatası alırsanız burayı açabilirsiniz
public class ReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;

    // 1. Frontend tablosu için JSON verisi dönen endpoint
    @GetMapping("/pivot")
    public ResponseEntity<ReportResponseDTO> getPivotReport(
            @RequestParam HareketTuruEnum hareketTuru,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        ReportResponseDTO report = reportService.generatePivotReport(hareketTuru, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // 2. Excel dosyası indirme (Export) endpointi
    @GetMapping("/pivot/excel")
    public ResponseEntity<byte[]> downloadPivotExcel(
            @RequestParam HareketTuruEnum hareketTuru,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            // Önce veriyi oluştur, sonra Excel'e çevir
            ReportResponseDTO report = reportService.generatePivotReport(hareketTuru, startDate, endDate);
            String baslik = hareketTuru.name() + " Malzeme Raporu";
            byte[] excelData = excelExportService.exportPivotToExcel(report, baslik);

            // Dosya indirme başlıkları (Headers) ayarlanıyor
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            headers.setContentDispositionFormData("attachment", "Hareket_Raporu.xlsx");            

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}