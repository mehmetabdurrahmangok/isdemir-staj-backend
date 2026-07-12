package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.malzeme.MalzemeCreateDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeDetayResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeUpdateDto;
import com.isdemirstaj.backend.entity.MalzemeEntity;
import com.isdemirstaj.backend.entity.MalzemeHareketEntity;
import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import com.isdemirstaj.backend.repository.MalzemeHareketRepository;
import com.isdemirstaj.backend.repository.MalzemeRepository;
import com.isdemirstaj.backend.repository.MalzemeTurRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalzemeService { // Malzeme servisi, malzeme ile ilgili iş mantığını içerir ve MalzemeRepository'yi kullanır.
    
    private final MalzemeRepository malzemeRepository; // MalzemeRepository'yi kullanmak için bir alan tanımlanır.
    private final MalzemeTurRepository malzemeTurRepository; // malzeme tür repository si
    private final MalzemeHareketRepository malzemeHareketRepository;

    public MalzemeService(MalzemeRepository malzemeRepository,
                            MalzemeTurRepository malzemeTurRepository,
                            MalzemeHareketRepository malzemeHareketRepository) { // MalzemeService sınıfının yapıcı metodu, MalzemeRepository'yi alır ve alanı başlatır.
        this.malzemeRepository = malzemeRepository;
        this.malzemeTurRepository = malzemeTurRepository;
        this.malzemeHareketRepository = malzemeHareketRepository;
    }

    // Bütün malzemeleri listeleyen ve döndüren metod
    public List<MalzemeResponseDto> getAllMalzemeler() {
        List<MalzemeEntity> malzemeler = malzemeRepository.findAll();
        
        return malzemeler.stream()
                .map(malzeme -> {
                    // Her malzeme için veritabanından güncel stok durumunu anlık olarak çekiyoruz
                    BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(malzeme.getId());
                    
                    return new MalzemeResponseDto(
                        malzeme.getId(),
                        malzeme.getMalzemeKodu(),
                        malzeme.getMalzemeAdi(),
                        malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getId() : null,
                        malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getMalzemeTurAdi() : "-",
                        malzeme.getMensei() != null ? malzeme.getMensei().name() : "-",
                        anlikStok // Dinamik hesaplanan değer DTO'ya eklendi
                    );
                })
                .collect(Collectors.toList());

    } // bütün malzemeleri listeleyen ve döndüren metod

    public MalzemeResponseDto createMalzeme(MalzemeCreateDto malzemeCreateDto) {

        MalzemeTurEntity malzemeTur = malzemeTurRepository.findById(malzemeCreateDto.getMalzemeTurId())
            .orElseThrow(() -> new RuntimeException("Belirtilen id'ye uygun malzeme türü bulunamadı"));

        MalzemeEntity malzemeEntity = new MalzemeEntity(
            malzemeCreateDto.getMalzemeKodu(),
            malzemeCreateDto.getMalzemeAdi(),
            malzemeTur,
            malzemeCreateDto.getMensei()
        );
        malzemeEntity.setOper(malzemeCreateDto.getOper());
        // Orijinal kodda kayıt işlemi eksikti, veritabanına kaydetmek için save metodunu çağırıyoruz.
        malzemeRepository.save(malzemeEntity);

        return new MalzemeResponseDto(
            malzemeEntity.getId(),
            malzemeEntity.getMalzemeKodu(),
            malzemeEntity.getMalzemeAdi(),
            malzemeTur.getId(),
            malzemeTur.getMalzemeTurAdi(),
            malzemeEntity.getMensei().name(),
            BigDecimal.ZERO
        );
    }

    // Malzeme silen metod
    public void deleteMalzeme(Long id) {
        if (!malzemeRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen malzeme bulunamadı. ID: " + id);
        }
        malzemeRepository.deleteById(id);
    }

    // Malzeme güncelleyen metod
    public MalzemeResponseDto updateMalzeme(Long id, MalzemeUpdateDto updateDto) {
        // 1. Güncellenecek malzemeyi veritabanından bul
        MalzemeEntity mevcutMalzeme = malzemeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Güncellenecek malzeme bulunamadı. ID: " + id));

        // 2. Yeni atanan malzeme türünü doğrula
        MalzemeTurEntity malzemeTur = malzemeTurRepository.findById(updateDto.getMalzemeTurId())
            .orElseThrow(() -> new RuntimeException("Belirtilen id'ye uygun malzeme türü bulunamadı"));

        // 3. Mevcut malzemenin verilerini yeni gelen verilerle değiştir
        mevcutMalzeme.setMalzemeKodu(updateDto.getMalzemeKodu());
        mevcutMalzeme.setMalzemeAdi(updateDto.getMalzemeAdi());
        mevcutMalzeme.setMalzemeTur(malzemeTur);
        mevcutMalzeme.setMensei(updateDto.getMensei());
        mevcutMalzeme.setOper(updateDto.getOper());
        
        // 4. Veritabanına kaydet
        malzemeRepository.save(mevcutMalzeme);

        // 5. güncel stok miktarını veri tabanından anlık olarak hesaplat
        BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(id);

        // 5. Güncellenmiş veriyi ResponseDto olarak geri dön
        return new MalzemeResponseDto(
            mevcutMalzeme.getId(),
            mevcutMalzeme.getMalzemeKodu(),
            mevcutMalzeme.getMalzemeAdi(),
            malzemeTur.getId(),
            malzemeTur.getMalzemeTurAdi(),
            mevcutMalzeme.getMensei().name(),
            anlikStok
        );
    }

    // Malzeme kodundan malzeme detaylarına getiren fonksiyon 
    public MalzemeDetayResponseDto getMalzemeDetayByKodu(String malzemeKodu) {
        // malzemeyi koduna göre veri tabanından getir
        MalzemeEntity malzeme = malzemeRepository.findByMalzemeKodu(malzemeKodu)
            .orElseThrow(() -> new RuntimeException("bu koda sahip bir malzeme bulunamadı"));

        // Anlık stok hesaplama
        BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(malzeme.getId());

        // Malzemeye ait hareketleri bul ve DTO oluştur
        List<MalzemeHareketEntity> hareketEntities = malzemeHareketRepository.findByMalzemeId(malzeme.getId());

        List<MalzemeDetayResponseDto.HareketDetayDto> hareketler = hareketEntities.stream()
            .map(h -> new MalzemeDetayResponseDto.HareketDetayDto(
                h.getId(),
                h.getHareketTarihi(),
                h.getMiktar(),
                h.getHareketTuru().toString()
            )).toList();

        // herşeyi birleştir ve döndür
        return new MalzemeDetayResponseDto(
            malzeme.getId(),
            malzeme.getMalzemeKodu(),
            malzeme.getMalzemeAdi(),
            malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getId() : null,
            malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getMalzemeTurAdi() : "-",
            malzeme.getMensei() != null ? malzeme.getMensei().name() : "-",
            anlikStok,
            hareketler
        );
    }

}
