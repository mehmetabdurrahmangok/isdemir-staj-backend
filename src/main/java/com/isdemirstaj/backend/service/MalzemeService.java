package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.malzeme.MalzemeCreateDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeDetayResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeUpdateDto;
import com.isdemirstaj.backend.entity.MalzemeDetayViewEntity;
import com.isdemirstaj.backend.entity.MalzemeEntity;
import com.isdemirstaj.backend.entity.MalzemeHareketEntity;
import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import com.isdemirstaj.backend.exception.ResourceNotFoundException;
import com.isdemirstaj.backend.repository.MalzemeDetayViewRepository;
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
    private final MalzemeDetayViewRepository malzemeDetayViewRepository;

    public MalzemeService(MalzemeRepository malzemeRepository,
                            MalzemeTurRepository malzemeTurRepository,
                            MalzemeHareketRepository malzemeHareketRepository,
                            MalzemeDetayViewRepository malzemeDetayViewRepository) { // MalzemeService sınıfının yapıcı metodu, MalzemeRepository'yi alır ve alanı başlatır.
        this.malzemeRepository = malzemeRepository;
        this.malzemeTurRepository = malzemeTurRepository;
        this.malzemeHareketRepository = malzemeHareketRepository;
        this.malzemeDetayViewRepository = malzemeDetayViewRepository;
    }

    // Bütün malzemeleri listeleyen ve döndüren metod
    public List<MalzemeResponseDto> getAllMalzemeler() {
        // ESKİ HALİ: Her kayıt için ayrı ayrı malzeme.getMalzemeTur().getMalzemeTurAdi() yapıyordun ve null kontrolleri vardı.
// YENİ HALİ: Null kontrolü ve JOIN'ler veritabanında çözüldüğü için kod tertemiz oldu!
List<MalzemeDetayViewEntity> viewListesi = malzemeDetayViewRepository.findAll();

return viewListesi.stream().map(v -> {
    BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(v.getId());
    if (anlikStok == null) {
        anlikStok = BigDecimal.ZERO;
    }
    return new MalzemeResponseDto(
        v.getId(), v.getMalzemeKodu(), v.getMalzemeAdi(), 
        v.getTurId(), v.getTurAdi(), v.getMensei(), 
        anlikStok, v.getOper(), v.getUpdatedAt()
    );
}).collect(Collectors.toList());

    } // bütün malzemeleri listeleyen ve döndüren metod

    public MalzemeResponseDto createMalzeme(MalzemeCreateDto malzemeCreateDto) {

        MalzemeTurEntity malzemeTur = malzemeTurRepository.findById(malzemeCreateDto.getMalzemeTurId())
            .orElseThrow(() -> new ResourceNotFoundException("Belirtilen id'ye uygun malzeme türü bulunamadı"));

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
            BigDecimal.ZERO,
            malzemeEntity.getOper(),
            malzemeEntity.getUpdatedAt()
        );
    }

    // (ESKİ!!)Malzeme silen metod
    /* 
    public void deleteMalzeme(Long id) {
        if (!malzemeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silinmek istenen malzeme bulunamadı. ID: " + id);
        }
        malzemeRepository.deleteById(id);
    }
    */
    // yeni silme metodumuz veri tabanındaki veri silme prosedürünü çağırıyor
    public void deleteMalzeme(Long id) {
        if (!malzemeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silinmek istenen malzeme bulunamadı!! ID: " + id);
        } 
        // veri tabanında malzeme varsa siliyoruz
        malzemeRepository.prosedurIleSil(id);
    }

    // Malzeme güncelleyen metod
    public MalzemeResponseDto updateMalzeme(Long id, MalzemeUpdateDto updateDto) {
        // 1. Güncellenecek malzemeyi veritabanından bul
        MalzemeEntity mevcutMalzeme = malzemeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek malzeme bulunamadı. ID: " + id));

        // 2. Yeni atanan malzeme türünü doğrula
        MalzemeTurEntity malzemeTur = malzemeTurRepository.findById(updateDto.getMalzemeTurId())
            .orElseThrow(() -> new ResourceNotFoundException("Belirtilen id'ye uygun malzeme türü bulunamadı"));

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
            anlikStok,
            mevcutMalzeme.getOper(),
            mevcutMalzeme.getUpdatedAt()
        );
    }

    // Malzeme kodundan malzeme detaylarına getiren fonksiyon 
        public MalzemeDetayResponseDto getMalzemeDetayByKodu(String malzemeKodu) {
        // VIEW'DAN ÇEKİYORUZ
        MalzemeDetayViewEntity v = malzemeDetayViewRepository.findByMalzemeKodu(malzemeKodu)
            .orElseThrow(() -> new ResourceNotFoundException("Bu koda sahip bir malzeme bulunamadı"));

        BigDecimal anlikStok = malzemeHareketRepository.hesaplaMevcutStok(v.getId());
        List<MalzemeHareketEntity> hareketEntities = malzemeHareketRepository.findByMalzemeId(v.getId());

        List<MalzemeDetayResponseDto.HareketDetayDto> hareketler = hareketEntities.stream()
            .map(h -> new MalzemeDetayResponseDto.HareketDetayDto(
                h.getId(), h.getHareketTarihi(), h.getMiktar(),
                h.getHareketTuru().toString(), h.getOper(), h.getUpdatedAt()
            )).toList();

        return new MalzemeDetayResponseDto(
            v.getId(), v.getMalzemeKodu(), v.getMalzemeAdi(),
            v.getTurId(), v.getTurAdi() != null ? v.getTurAdi() : "-",
            v.getMensei() != null ? v.getMensei() : "-",
            anlikStok, v.getOper(), v.getUpdatedAt(), hareketler
        );
    }

    // =================================================================
    // YAPAY ZEKA (AI) İÇİN EKLENEN YENİ FİLTRELEME SERVİSLERİ
    // =================================================================
    public List<MalzemeResponseDto> getMalzemelerByTurAdi(String turAdi) {
        return getAllMalzemeler().stream()
                .filter(dto -> dto.getMalzemeTurAdi() != null && dto.getMalzemeTurAdi().equalsIgnoreCase(turAdi))
                .collect(Collectors.toList());
    }

    public List<MalzemeResponseDto> getMalzemelerByMensei(String mensei) {
        return getAllMalzemeler().stream()
                .filter(dto -> dto.getMensei() != null && dto.getMensei().equalsIgnoreCase(mensei))
                .collect(Collectors.toList());
    }

    public List<MalzemeResponseDto> getKritikStokMalzemeler() {
        return getAllMalzemeler().stream()
                .filter(dto -> dto.getMevcutMiktar() != null && dto.getMevcutMiktar().compareTo(BigDecimal.ZERO) <= 0)
                .collect(Collectors.toList());
    }

}
