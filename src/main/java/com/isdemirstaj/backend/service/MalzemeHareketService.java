package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.repository.MalzemeHareketRepository;
import com.isdemirstaj.backend.entity.MalzemeHareketEntity;
import com.isdemirstaj.backend.dto.MalzemeHareketResponseDto;   

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalzemeHareketService {
    private final MalzemeHareketRepository malzemeHareketRepository;

    public MalzemeHareketService(MalzemeHareketRepository malzemeHareketRepository) {
        this.malzemeHareketRepository = malzemeHareketRepository;
    }

    // Bütün malzeme hareketlerini listeleyen ve döndüren metod
    public List<MalzemeHareketResponseDto> getAllMalzemeHareketleri() {
        List<MalzemeHareketEntity> malzemeHareketleri = malzemeHareketRepository.findAll();

        return malzemeHareketleri.stream()
                .map(malzemeHareket -> new MalzemeHareketResponseDto(
                    malzemeHareket.getMalzeme().getId(),
                    malzemeHareket.getHareketTarihi(),
                    malzemeHareket.getMiktar(),
                    malzemeHareket.getHareketTuru().toString()
                ))
                .collect(Collectors.toList());
    }
    
}
