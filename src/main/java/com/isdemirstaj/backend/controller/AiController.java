package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.malzeme.MalzemeDetayResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzemeTur.MalzemeTurResponseDto;
import com.isdemirstaj.backend.dto.report.ReportResponseDTO;
import com.isdemirstaj.backend.dto.report.ReportRowDTO;
import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;
import com.isdemirstaj.backend.service.MalzemeService;
import com.isdemirstaj.backend.service.MalzemeTurService;
import com.isdemirstaj.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final MalzemeService malzemeService;
    private final MalzemeTurService malzemeTurService;
    private final ReportService reportService;

    // =========================================================
    // 1. TÜM MALZEMELERİ LİSTELE
    // GET /api/ai/malzemeler
    // =========================================================
    @GetMapping(value = "/malzemeler", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String tumMalzemeler() {
        List<MalzemeResponseDto> liste = malzemeService.getAllMalzemeler();
        if (liste.isEmpty()) return "Sistemde hiç malzeme bulunamadı.";

        StringBuilder sb = new StringBuilder();
        sb.append("Toplam ").append(liste.size()).append(" malzeme bulundu:\n\n");
        for (MalzemeResponseDto m : liste) {
            sb.append("- ").append(m.getMalzemeKodu())
              .append(" | ").append(m.getMalzemeAdi())
              .append(" | Tür: ").append(safe(m.getMalzemeTurAdi()))
              .append(" | Menşei: ").append(safe(m.getMensei()))
              .append(" | Mevcut Miktar: ").append(m.getMevcutMiktar() != null ? m.getMevcutMiktar() : 0)
              .append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    // 2. MALZEME DETAYI + HAREKET GEÇMİŞİ
    // GET /api/ai/detay/{kodu}
    // =========================================================
    @GetMapping(value = "/detay/{kodu}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String malzemeDetay(@PathVariable String kodu) {
        try {
            MalzemeDetayResponseDto d = malzemeService.getMalzemeDetayByKodu(kodu);
            StringBuilder sb = new StringBuilder();
            sb.append("Malzeme: ").append(d.getMalzemeKodu()).append(" - ").append(d.getMalzemeAdi()).append("\n");
            sb.append("Tür: ").append(safe(d.getMalzemeTurAdi())).append("\n");
            sb.append("Menşei: ").append(safe(d.getMensei())).append("\n");
            sb.append("Mevcut Miktar: ").append(d.getMevcutMiktar() != null ? d.getMevcutMiktar() : 0).append("\n\n");

            List<MalzemeDetayResponseDto.HareketDetayDto> hareketler = d.getHareketler();
            if (hareketler == null || hareketler.isEmpty()) {
                sb.append("Bu malzemenin hareket geçmişi bulunmamaktadır.");
            } else {
                sb.append("Hareket Geçmişi (").append(hareketler.size()).append(" kayıt):\n");
                for (MalzemeDetayResponseDto.HareketDetayDto h : hareketler) {
                    String tarih = h.getHareketTarihi() != null
                        ? h.getHareketTarihi().toLocalDate().toString() : "-";
                    sb.append("- ").append(tarih)
                      .append(" tarihinde ").append(h.getMiktar() != null ? h.getMiktar() : 0)
                      .append(" adet ").append(safe(h.getHareketTuru()))
                      .append(" işlemi\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "HATA: " + kodu + " kodlu malzeme bulunamadı.";
        }
    }

    // =========================================================
    // 3. TÜRE GÖRE MALZEMELER
    // GET /api/ai/tur/{turAdi}
    // =========================================================
    @GetMapping(value = "/tur/{turAdi}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String tureMalzemeler(@PathVariable String turAdi) {
        List<MalzemeResponseDto> liste = malzemeService.getMalzemelerByTurAdi(turAdi);
        if (liste.isEmpty()) return turAdi + " türünde malzeme bulunamadı.";

        StringBuilder sb = new StringBuilder();
        sb.append(turAdi).append(" türünde ").append(liste.size()).append(" malzeme bulundu:\n\n");
        for (MalzemeResponseDto m : liste) {
            sb.append("- ").append(m.getMalzemeKodu())
              .append(" | ").append(m.getMalzemeAdi())
              .append(" | Mevcut Miktar: ").append(m.getMevcutMiktar() != null ? m.getMevcutMiktar() : 0)
              .append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    // 4. KRİTİK STOK
    // GET /api/ai/kritik-stok
    // =========================================================
    @GetMapping(value = "/kritik-stok", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String kritikStok() {
        List<MalzemeResponseDto> liste = malzemeService.getKritikStokMalzemeler();
        if (liste.isEmpty()) return "Şu an kritik stok seviyesinde hiçbir malzeme bulunmamaktadır.";

        StringBuilder sb = new StringBuilder();
        sb.append("KRİTİK STOK UYARISI (").append(liste.size()).append(" malzeme):\n\n");
        for (MalzemeResponseDto m : liste) {
            sb.append("- ").append(m.getMalzemeKodu())
              .append(" | ").append(m.getMalzemeAdi())
              .append(" | Tür: ").append(safe(m.getMalzemeTurAdi()))
              .append(" | Mevcut Miktar: ").append(m.getMevcutMiktar() != null ? m.getMevcutMiktar() : 0)
              .append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    // 5. MALZEME TÜRLERİ
    // GET /api/ai/turler
    // =========================================================
    @GetMapping(value = "/turler", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String turler() {
        List<MalzemeTurResponseDto> liste = malzemeTurService.getAllMalzemeTurleri();
        if (liste.isEmpty()) return "Sistemde tanımlı tür bulunamadı.";

        StringBuilder sb = new StringBuilder();
        sb.append("Sistemde ").append(liste.size()).append(" malzeme türü var:\n\n");
        for (MalzemeTurResponseDto t : liste) {
            sb.append("- ").append(t.getMalzemeTurAdi()).append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    // 6. TARİH ARASI PİVOT RAPORU
    // GET /api/ai/rapor?hareketTuru=GELEN&startDate=2026-07-01T00:00:00&endDate=2026-07-31T23:59:59
    // =========================================================
    @GetMapping(value = "/rapor", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String pivotRapor(
            @RequestParam HareketTuruEnum hareketTuru,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReportResponseDTO rapor = reportService.generatePivotReport(hareketTuru, startDate, endDate);
        List<ReportRowDTO> rows = rapor.getRows();

        StringBuilder sb = new StringBuilder();
        sb.append(hareketTuru.name()).append(" İŞLEMLERİ (")
          .append(startDate.toLocalDate()).append(" - ").append(endDate.toLocalDate()).append("):\n");

        Map<String, List<String>> malzemeDetaylar = new LinkedHashMap<>();
        Map<String, BigDecimal> malzemeToplamlar = new LinkedHashMap<>();

        boolean veriVar = false;
        for (ReportRowDTO row : rows) {
            if (row.getTarih() == null || row.getValues() == null) continue;

            for (Map.Entry<String, BigDecimal> entry : row.getValues().entrySet()) {
                String isim = entry.getKey();
                if (isim.endsWith(" Toplam")) continue;

                BigDecimal val = entry.getValue();
                if (val != null && val.compareTo(BigDecimal.ZERO) > 0) {
                    veriVar = true;
                    malzemeToplamlar.put(isim, malzemeToplamlar.getOrDefault(isim, BigDecimal.ZERO).add(val));
                    malzemeDetaylar.computeIfAbsent(isim, k -> new ArrayList<>())
                                  .add(row.getTarih() + " tarihinde " + val + " adet");
                }
            }
        }

        if (!veriVar) {
            return "Bu tarih aralığında " + hareketTuru.name() + " türünde hiçbir hareket bulunamadı.";
        }

        for (Map.Entry<String, BigDecimal> entry : malzemeToplamlar.entrySet()) {
            String isim = entry.getKey();
            BigDecimal toplamMiktar = entry.getValue();
            List<String> detaylar = malzemeDetaylar.get(isim);

            sb.append("- ").append(isim).append(": Toplam ").append(toplamMiktar).append(" adet");
            if (detaylar != null && !detaylar.isEmpty()) {
                sb.append(" (Detay: ").append(String.join(", ", detaylar)).append(")");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // =========================================================
    // 7. TÜRE GÖRE TOPLU HAREKET GEÇMİŞİ
    // GET /api/ai/tur-hareket/{turAdi}
    // =========================================================
    @GetMapping(value = "/tur-hareket/{turAdi}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String tureGoreTopluHareket(@PathVariable String turAdi) {
        List<MalzemeResponseDto> malzemeler = malzemeService.getMalzemelerByTurAdi(turAdi);
        if (malzemeler.isEmpty()) return turAdi + " türünde malzeme bulunamadı.";

        StringBuilder sb = new StringBuilder();
        sb.append(turAdi).append(" türündeki malzemelerin hareket geçmişi:\n\n");

        for (MalzemeResponseDto m : malzemeler) {
            try {
                MalzemeDetayResponseDto detay = malzemeService.getMalzemeDetayByKodu(m.getMalzemeKodu());
                sb.append(m.getMalzemeKodu()).append(" - ").append(m.getMalzemeAdi()).append(":\n");

                List<MalzemeDetayResponseDto.HareketDetayDto> hareketler = detay.getHareketler();
                if (hareketler == null || hareketler.isEmpty()) {
                    sb.append("  Hareket yok.\n\n");
                } else {
                    for (MalzemeDetayResponseDto.HareketDetayDto h : hareketler) {
                        String tarih = h.getHareketTarihi() != null
                            ? h.getHareketTarihi().toLocalDate().toString() : "-";
                        sb.append("  - ").append(tarih)
                          .append(" tarihinde ").append(h.getMiktar() != null ? h.getMiktar() : 0)
                          .append(" adet ").append(safe(h.getHareketTuru())).append("\n");
                    }
                    sb.append("\n");
                }
            } catch (Exception e) {
                sb.append(m.getMalzemeKodu()).append(": Detay alınamadı.\n\n");
            }
        }
        return sb.toString();
    }

    // =========================================================
    // 8. MENŞEİYE GÖRE MALZEMELER
    // GET /api/ai/mensei/{mensei}
    // =========================================================
    @GetMapping(value = "/mensei/{mensei}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String menseiMalzemeler(@PathVariable String mensei) {
        List<MalzemeResponseDto> liste = malzemeService.getMalzemelerByMensei(mensei);
        if (liste.isEmpty()) return mensei + " menşeili malzeme bulunamadı.";

        StringBuilder sb = new StringBuilder();
        sb.append(mensei).append(" menşeili ").append(liste.size()).append(" malzeme bulundu:\n\n");
        for (MalzemeResponseDto m : liste) {
            sb.append("- ").append(m.getMalzemeKodu())
              .append(" | ").append(m.getMalzemeAdi())
              .append(" | Tür: ").append(safe(m.getMalzemeTurAdi()))
              .append(" | Mevcut Miktar: ").append(m.getMevcutMiktar() != null ? m.getMevcutMiktar() : 0)
              .append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    // YARDIMCI
    // =========================================================
    private String safe(String val) {
        return val != null ? val : "-";
    }
}
