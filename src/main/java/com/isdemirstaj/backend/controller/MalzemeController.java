package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.MalzemeResponseDto;
import com.isdemirstaj.backend.service.MalzemeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin // bir porttan diğerine istek atabilmek için
@RestController
@RequestMapping("/api/malzemeler")
public class MalzemeController {
    
    private final MalzemeService malzemeService;

    public MalzemeController(MalzemeService malzemeService) {
        this.malzemeService = malzemeService;
    }

    // Bütün malzemeleri listeleyen endpoint
    @GetMapping
    public List<MalzemeResponseDto> getMalzeme() {
        return malzemeService.getAllMalzemeler();
    }

    // ilerde buraya malzeme kodu ile malzeme ve türünü getiren bir endpoint eklenecek

    // buraya silme ekleme ve güncelleme endpointleri eklenecek
}
