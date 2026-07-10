package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.MalzemeResponseDto;
import com.isdemirstaj.backend.entity.MalzemeEntity;
import com.isdemirstaj.backend.repository.MalzemeRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalzemeService {
    
    private final MalzemeRepository malzemeRepository;

    public MalzemeService(MalzemeRepository malzemeRepository) {
        this.malzemeRepository = malzemeRepository;
    }

    // Bütün malzemeleri listeleyen ve döndüren metod
    public List<MalzemeResponseDto> getAllMalzemeler() {
        List<MalzemeEntity> malzemeler = malzemeRepository.findAll();

        return malzemeler.stream()
                .map(malzeme -> new MalzemeResponseDto(
                    malzeme.getMalzemeKodu(),
                    malzeme.getMalzemeAdi(),
                    malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getId() : null,
                    // eğer malzeme türü null ise "-" döndür, değilse tür adını döndür
                    malzeme.getMalzemeTur() != null ? malzeme.getMalzemeTur().getMalzemeTurAdi() : "-",
                    // eğer mensei null ise "-" döndür, değilse enum değerini döndür
                    malzeme.getMensei() != null ? malzeme.getMensei().name() : "-",
                    malzeme.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }


}
