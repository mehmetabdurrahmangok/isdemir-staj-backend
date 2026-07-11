package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.MalzemeResponseDto;
import com.isdemirstaj.backend.service.MalzemeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin // bir porttan diğerine istek atabilmek için
@RestController // bu sınıfın controller olduğunu belirtir
@RequestMapping("/api/malzemeler") // bu controllerin hangi endpoint ile ilişkilendirileceğini belirtir
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
