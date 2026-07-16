package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.repository.MalzemeHareketRepository;
import com.isdemirstaj.backend.repository.MalzemeRepository;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketCreateDto;
import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketResponseDto;
import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketUpdateDto;
import com.isdemirstaj.backend.entity.MalzemeEntity;
import com.isdemirstaj.backend.entity.MalzemeHareketEntity;
import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class MalzemeHareketService {
    private final MalzemeHareketRepository malzemeHareketRepository;
    private final MalzemeRepository malzemeRepository;

    public MalzemeHareketService(MalzemeHareketRepository malzemeHareketRepository, MalzemeRepository malzemeRepository) {
        this.malzemeHareketRepository = malzemeHareketRepository;
        this.malzemeRepository = malzemeRepository;
    }

    // Bütün malzeme hareketlerini listeleyen ve döndüren metod
    public List<MalzemeHareketResponseDto> getAllMalzemeHareketleri() {
        List<MalzemeHareketEntity> malzemeHareketleri = malzemeHareketRepository.findAll();

        return malzemeHareketleri.stream()
                .map(malzemeHareket -> {
                    // Malzemenin güncel stok durumunu anlık olarak çekiyoruz
                    BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(malzemeHareket.getMalzeme().getId());
                    
                    return new MalzemeHareketResponseDto(
                        malzemeHareket.getId(),
                        new MalzemeResponseDto(
                            malzemeHareket.getMalzeme().getId(),
                            malzemeHareket.getMalzeme().getMalzemeKodu(),
                            malzemeHareket.getMalzeme().getMalzemeAdi(),
                            malzemeHareket.getMalzeme().getMalzemeTur() != null ? malzemeHareket.getMalzeme().getMalzemeTur().getId() : null,
                            malzemeHareket.getMalzeme().getMalzemeTur() != null ? malzemeHareket.getMalzeme().getMalzemeTur().getMalzemeTurAdi() : "-",
                            malzemeHareket.getMalzeme().getMensei() != null ? malzemeHareket.getMalzeme().getMensei().name() : "-",
                            anlikStok, // 7. Parametre eklendi
                            malzemeHareket.getMalzeme().getOper(),
                            malzemeHareket.getMalzeme().getUpdatedAt()
                        ),
                        malzemeHareket.getHareketTarihi(),
                        malzemeHareket.getMiktar(),
                        malzemeHareket.getHareketTuru().toString(),
                        malzemeHareket.getOper(),
                        malzemeHareket.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    // --- HAREKET EKLEME İŞLEMİ ---
    public MalzemeHareketResponseDto createMalzemeHareket(MalzemeHareketCreateDto createDto) {
        // İlgili malzemeyi bul
        MalzemeEntity malzeme = malzemeRepository.findById(createDto.getMalzemeId())
            .orElseThrow(() -> new RuntimeException("Belirtilen id'ye uygun malzeme bulunamadı"));

        MalzemeHareketEntity yeniHareket = new MalzemeHareketEntity();
        yeniHareket.setMalzeme(malzeme);
        yeniHareket.setHareketTarihi(createDto.getHareketTarihi());
        yeniHareket.setMiktar(createDto.getMiktar());
        yeniHareket.setHareketTuru(HareketTuruEnum.valueOf(createDto.getHareketTuru().toUpperCase()));
        yeniHareket.setOper(createDto.getOper());

        malzemeHareketRepository.save(yeniHareket);
        BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(malzeme.getId());

        // İç içe geçmiş (nested) DTO yapımızla yanıtı dönüyoruz
        return new MalzemeHareketResponseDto(
            yeniHareket.getId(),
            new MalzemeResponseDto(
                malzeme.getId(),
                malzeme.getMalzemeKodu(),
                malzeme.getMalzemeAdi(),
                malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getId() : null,
                malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getMalzemeTurAdi() : "-",
                malzeme.getMensei() != null ? malzeme.getMensei().name() : "-",
                anlikStok,
                malzeme.getOper(),
                malzeme.getUpdatedAt()
            ),
            yeniHareket.getHareketTarihi(),
            yeniHareket.getMiktar(),
            yeniHareket.getHareketTuru().toString(),
            yeniHareket.getOper(),
            yeniHareket.getUpdatedAt()
        );
    }

    // --- HAREKET SİLME İŞLEMİ ---
    public void deleteMalzemeHareket(Long id) {
        if (!malzemeHareketRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen hareket bulunamadı. ID: " + id);
        }
        malzemeHareketRepository.deleteById(id);
    }

    // --- HAREKET GÜNCELLEME İŞLEMİ ---
    public MalzemeHareketResponseDto updateMalzemeHareket(Long id, MalzemeHareketUpdateDto updateDto) {
        MalzemeHareketEntity mevcutHareket = malzemeHareketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Güncellenecek hareket bulunamadı. ID: " + id));

        // Eğer harekete bağlı malzeme de değiştirilmek isteniyorsa onu bul
        if (!mevcutHareket.getMalzeme().getId().equals(updateDto.getMalzemeId())) {
            MalzemeEntity yeniMalzeme = malzemeRepository.findById(updateDto.getMalzemeId())
                .orElseThrow(() -> new RuntimeException("Belirtilen id'ye uygun malzeme bulunamadı"));
            mevcutHareket.setMalzeme(yeniMalzeme);
        }

        mevcutHareket.setHareketTarihi(updateDto.getHareketTarihi());
        mevcutHareket.setMiktar(updateDto.getMiktar());
        
        // Enum varsayımı üzerinden hareket türünü ayarlıyoruz
        // (HareketTuruEnum adında bir enum kullandığını varsayarak valueOf ile dönüştürüyoruz)
        mevcutHareket.setHareketTuru(HareketTuruEnum.valueOf(updateDto.getHareketTuru().toUpperCase()));
        mevcutHareket.setOper(updateDto.getOper());

        malzemeHareketRepository.save(mevcutHareket);

        BigDecimal anlikHareket = malzemeHareketRepository.hesaplaMevcutStok(mevcutHareket.getMalzeme().getId());

        // Güncel veriyi Response DTO'ya dönüştürüp geri yolluyoruz
        return new MalzemeHareketResponseDto(
            mevcutHareket.getId(),
            new MalzemeResponseDto(
                mevcutHareket.getMalzeme().getId(),
                mevcutHareket.getMalzeme().getMalzemeKodu(),
                mevcutHareket.getMalzeme().getMalzemeAdi(),
                mevcutHareket.getMalzeme().getMalzemeTur() != null ? mevcutHareket.getMalzeme().getMalzemeTur().getId() : null,
                mevcutHareket.getMalzeme().getMalzemeTur() != null ? mevcutHareket.getMalzeme().getMalzemeTur().getMalzemeTurAdi() : "-",
                mevcutHareket.getMalzeme().getMensei() != null ? mevcutHareket.getMalzeme().getMensei().name() : "-",
                anlikHareket,
                mevcutHareket.getMalzeme().getOper(),
                mevcutHareket.getMalzeme().getUpdatedAt()
            ),
            mevcutHareket.getHareketTarihi(),
            mevcutHareket.getMiktar(),
            mevcutHareket.getHareketTuru().toString(),
            mevcutHareket.getOper(),
            mevcutHareket.getUpdatedAt()
        );
    }
    
}
