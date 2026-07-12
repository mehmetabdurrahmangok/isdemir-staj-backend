package com.isdemirstaj.backend.service;

import org.springframework.stereotype.Service;

import com.isdemirstaj.backend.dto.malzemeTur.MalzemeTurCreateDto;
import com.isdemirstaj.backend.dto.malzemeTur.MalzemeTurResponseDto;
import com.isdemirstaj.backend.dto.malzemeTur.MalzemeTurUpdateDto;
import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import com.isdemirstaj.backend.repository.MalzemeTurRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalzemeTurService { // Malzeme türleri ile ilgili işlemleri gerçekleştiren servis sınıfı
    private final MalzemeTurRepository malzemeTurRepository;

    public MalzemeTurService(MalzemeTurRepository malzemeTurRepository) {
        this.malzemeTurRepository = malzemeTurRepository;
    }

    // Bütün malzeme türlerini listeleyen ve döndüren metod
    public List<MalzemeTurResponseDto> getAllMalzemeTurleri() {
        List<MalzemeTurEntity> malzemeTurleri = malzemeTurRepository.findAll();

        return malzemeTurleri.stream()
                .map(malzemeTur -> new MalzemeTurResponseDto(
                    malzemeTur.getId(),
                    malzemeTur.getMalzemeTurAdi()
                ))
                .collect(Collectors.toList());
    }

    // malzeme türü ekleme metodu
    public MalzemeTurResponseDto createMalzemeTur(MalzemeTurCreateDto createDto) {
        MalzemeTurEntity malzemeTur = new MalzemeTurEntity();
        malzemeTur.setMalzemeTurAdi(createDto.getMalzemeTurAdi());
        malzemeTur.setOper(createDto.getOper());
        
        malzemeTurRepository.save(malzemeTur);

        return new MalzemeTurResponseDto(
            malzemeTur.getId(),
            malzemeTur.getMalzemeTurAdi()
        );
    }

    // malzeme türü silme
    public void deleteMalzemeTur(Long id) {
        if(!malzemeTurRepository.existsById(id)) {
            throw new RuntimeException("Silinmek istenen malzeme bulunamadı");
        }
        malzemeTurRepository.deleteById(id);
    }

    // Malzeme türü güncelleme metodu
    public MalzemeTurResponseDto updateMalzemeTur(Long id, MalzemeTurUpdateDto updateDto) {
        MalzemeTurEntity mevcutTur = malzemeTurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Güncellenecek malzeme türü bulunamadı"));

        mevcutTur.setMalzemeTurAdi(updateDto.getMalzemeTurAdi());
        mevcutTur.setOper(updateDto.getOper());

        malzemeTurRepository.save(mevcutTur);

        return new MalzemeTurResponseDto(
            mevcutTur.getId(),
            mevcutTur.getMalzemeTurAdi()
        );
    }
}
