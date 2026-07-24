package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.report.ReportColumnDTO;
import com.isdemirstaj.backend.dto.report.ReportResponseDTO;
import com.isdemirstaj.backend.dto.report.ReportRowDTO;
import com.isdemirstaj.backend.entity.*;
import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;
import com.isdemirstaj.backend.repository.MalzemeHareketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReportService {
    private final MalzemeHareketRepository malzemeHareketRepository;
    private final com.isdemirstaj.backend.repository.MalzemeRepository malzemeRepository;

    public ReportResponseDTO generatePivotReport(HareketTuruEnum hareketTuru,
        LocalDateTime start, LocalDateTime end) {

            // veri tabanından tarih aralığında istenen hareket türündeki verileri satır satır çekiyoruz
            List<MalzemeHareketEntity> hareketler = malzemeHareketRepository
                .findByHareketTuruAndHareketTarihiBetween(hareketTuru, start, end);

            // Tüm malzemeleri çekiyoruz ki hareket olmasa bile Excel tablosunda sütunlar eksiksiz ve düzgün çıksın
            List<MalzemeEntity> tumMalzemeler = malzemeRepository.findAll();
            Map<MalzemeTurEntity, Set<MalzemeEntity>> turBazliMalzemeler =
            tumMalzemeler.stream()
                .collect(Collectors.groupingBy(MalzemeEntity::getMalzemeTur,
                    Collectors.toSet()));

            // sütunları oluşturma
            List<ReportColumnDTO> columns = new ArrayList<>();
            for(Map.Entry<MalzemeTurEntity, Set<MalzemeEntity>> entry :
                turBazliMalzemeler.entrySet()) {
                    String turAdi = entry.getKey().getMalzemeTurAdi();

                    // önce türün içerisindeki malzemeler
                    for(MalzemeEntity m : entry.getValue()) {
                        columns.add(new ReportColumnDTO(m.getMalzemeAdi(), false, turAdi));
                    }
                    // tür toplam sütunu ekleniyor
                    columns.add(new ReportColumnDTO(turAdi + " Toplam", true, turAdi));
                }

                // en alt satırı oluşturuyoruz (0 ile başlıyor)
                Map<String, BigDecimal> rowTotal = new LinkedHashMap<>();
                for (ReportColumnDTO col : columns) {
                    rowTotal.put(col.getHeaderName(), BigDecimal.ZERO);
                }

                // her tarih için satır oluşturma

                // veri tabanındaki tarih verilerini gün gün grupla

                Map<LocalDate, List<MalzemeHareketEntity>> gunlukHareketler = 
                hareketler.stream()
                    .collect(Collectors.groupingBy(h -> 
                        h.getHareketTarihi().toLocalDate()));

                List<ReportRowDTO> rows = new ArrayList<>(); // satırlar listesi
                LocalDate currentDate = start.toLocalDate(); // anlık gün -> başlangıç tarihinden başlar
                LocalDate endDate = end.toLocalDate(); // bitiş tarihine kadar gider

                // seçilen tarih aralığındaki her gün için döngü
                while (!currentDate.isAfter(endDate)) {
                    ReportRowDTO row = new ReportRowDTO();

                    row.setTarih(currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                    // o günün hareketlerini al yoksa boş liste dön
                    List<MalzemeHareketEntity> oGununHareketleri = 
                    gunlukHareketler.getOrDefault(currentDate, Collections.emptyList());

                    // her bir tür ve o türdeki malzemeler için hücreleri doldur
                    for (Map.Entry<MalzemeTurEntity, Set<MalzemeEntity>> entry : turBazliMalzemeler.entrySet()) {
                        String turAdi = entry.getKey().getMalzemeTurAdi();
                        BigDecimal turGunlukToplam = BigDecimal.ZERO;
                        for (MalzemeEntity m : entry.getValue()) {
                            // O gün, o malzemeden yapılan toplam hareketi bul
                            BigDecimal miktar = oGununHareketleri.stream()
                                .filter(h -> h.getMalzeme().getId().equals(m.getId()))
                                .map(MalzemeHareketEntity::getMiktar)
                                .reduce(BigDecimal.ZERO, BigDecimal::add); // Topla
                            // Hücreye yaz (Örn: Kangal Demir = 1000)
                            row.getValues().put(m.getMalzemeAdi(), miktar);
                            turGunlukToplam = turGunlukToplam.add(miktar);
                            // En alt satır "Genel Toplam" sayacını da bu miktar kadar artır
                            rowTotal.put(m.getMalzemeAdi(), rowTotal.get(m.getMalzemeAdi()).add(miktar));
                        }
                        
                        // O türün günlük yatay toplamını hücreye yaz
                        String turToplam = turAdi + " Toplam";
                        row.getValues().put(turToplam, turGunlukToplam);

                        // en alt satırdaki toplam sayacını arttır
                        rowTotal.put(turToplam,
                            rowTotal.get(turToplam).add(turGunlukToplam));
                    }
                    rows.add(row);
                    currentDate = currentDate.plusDays(1); // bir sonraki güne geç
                }

                // Tüm sonuçları DTO olarak gönder
                ReportResponseDTO response = new ReportResponseDTO();
                response.setColumns(columns);
                response.setRows(rows);
                response.setRowTotal(rowTotal);
                return response;

        }

}
