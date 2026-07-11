package com.isdemirstaj.backend.service;

import org.springframework.stereotype.Service;

import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import com.isdemirstaj.backend.repository.MalzemeTurRepository;
import com.isdemirstaj.backend.dto.MalzemeTurResponseDto;

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
                    malzemeTur.getMalzemeTurAdi()
                ))
                .collect(Collectors.toList());
    }
}
