package com.isdemirstaj.backend.job;

import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.service.EmailService;
import com.isdemirstaj.backend.service.ExcelExportService;
import com.isdemirstaj.backend.service.MalzemeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MalzemeRaporJob {

    private final MalzemeService malzemeService;
    private final ExcelExportService excelExportService;
    private final EmailService emailService;

    public MalzemeRaporJob(MalzemeService malzemeService, ExcelExportService excelExportService, EmailService emailService) {
        this.malzemeService = malzemeService;
        this.excelExportService = excelExportService;
        this.emailService = emailService;
    }

    // Normalde saat başı için "0 0 * * * *" yazılır. Biz test için 1 dakikada bir (60000 ms) çalıştırıyoruz.
    // Sunumdan önce burayı örneğin her sabah 08:00 yapabilirsin.
    @Scheduled(cron = "0 0 * * * *") 
    public void saatlikRaporGonder() {
        System.out.println("⏳ [RAPOR JOB ÇALIŞTI] Veriler Excel'e dönüştürülüp Mail atılıyor...");
        try {
            // 1. Verileri View'dan ve Function'dan fişek gibi çek
            List<MalzemeResponseDto> malzemeler = malzemeService.getAllMalzemeler();

            // 2. Verileri Excel formatına dönüştür
            byte[] excelDosyasi = excelExportService.exportMalzemeListesiToExcel(malzemeler);

            // 3. E-postayı gönder
            String zaman = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            String konu = "Saatlik Malzeme Durum Raporu - " + zaman;
            String icerik = "Merhaba,\n\nFabrikadaki tüm malzemelerin güncel stok durumlarını içeren Excel raporu ektedir.\n\nİyi çalışmalar.";
            
            // Gmail hesabın kendi kendine mail atıyor :)
            emailService.sendExcelEmail("isdemirstaj@gmail.com", konu, icerik, excelDosyasi, "Stok_Raporu_" + zaman + ".xlsx");
            
        } catch (Exception e) {
            System.err.println("❌ Rapor job'ında bir hata oluştu: " + e.getMessage());
        }
    }
}