package com.isdemirstaj.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isdemirstaj.backend.dto.MalzemeHareketResponseDto;
import com.isdemirstaj.backend.service.MalzemeHareketService;

@CrossOrigin
@RestController
@RequestMapping("/api/malzemeHareketleri")
public class MalzemeHareketController {
    private final MalzemeHareketService malzemeHareketService;

    public MalzemeHareketController(MalzemeHareketService malzemeHareketService) {
        this.malzemeHareketService = malzemeHareketService;
    }

    // bütün malzeme hareketlerini listeleyen endpoint
    @GetMapping
    public List<MalzemeHareketResponseDto> getMalzemeHareketleri() {
        return malzemeHareketService.getAllMalzemeHareketleri();
    }
    
}
