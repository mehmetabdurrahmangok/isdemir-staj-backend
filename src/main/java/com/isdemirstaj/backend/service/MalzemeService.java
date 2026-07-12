package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.malzeme.MalzemeCreateDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeUpdateDto;
import com.isdemirstaj.backend.entity.MalzemeEntity;
import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import com.isdemirstaj.backend.repository.MalzemeRepository;
import com.isdemirstaj.backend.repository.MalzemeTurRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalzemeService { // Malzeme servisi, malzeme ile ilgili iş mantığını içerir ve MalzemeRepository'yi kullanır.
    
    private final MalzemeRepository malzemeRepository; // MalzemeRepository'yi kullanmak için bir alan tanımlanır.
    private final MalzemeTurRepository malzemeTurRepository; // malzeme tür repository si

    public MalzemeService(MalzemeRepository malzemeRepository, MalzemeTurRepository malzemeTurRepository) { // MalzemeService sınıfının yapıcı metodu, MalzemeRepository'yi alır ve alanı başlatır.
        this.malzemeRepository = malzemeRepository;
        this.malzemeTurRepository = malzemeTurRepository;
    }

    // Bütün malzemeleri listeleyen ve döndüren metod
    public List<MalzemeResponseDto> getAllMalzemeler() {
        List<MalzemeEntity> malzemeler = malzemeRepository.findAll();

        return malzemeler.stream()
                .map(malzeme -> new MalzemeResponseDto(
                    malzeme.getId(),
                    malzeme.getMalzemeKodu(),
                    malzeme.getMalzemeAdi(),
                    malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getId() : null,
                    // eğer malzeme türü null ise "-" döndür, değilse tür adını döndür
                    malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getMalzemeTurAdi() : "-",
                    // eğer mensei null ise "-" döndür, değilse enum değerini döndür
                    malzeme.getMensei() != null ? malzeme.getMensei().name() : "-"
                ))
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
            malzemeEntity.getMensei().name()
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

        // 5. Güncellenmiş veriyi ResponseDto olarak geri dön
        return new MalzemeResponseDto(
            mevcutMalzeme.getId(),
            mevcutMalzeme.getMalzemeKodu(),
            mevcutMalzeme.getMalzemeAdi(),
            malzemeTur.getId(),
            malzemeTur.getMalzemeTurAdi(),
            mevcutMalzeme.getMensei().name()
        );
    }


}
