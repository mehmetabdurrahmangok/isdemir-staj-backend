package com.isdemirstaj.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketCreateDto;
import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketResponseDto;
import com.isdemirstaj.backend.dto.malzemeHareket.MalzemeHareketUpdateDto;
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

    @PostMapping("/create")
    public ResponseEntity<MalzemeHareketResponseDto> createMalzemeHareket(@RequestBody MalzemeHareketCreateDto createDto) {
        MalzemeHareketResponseDto yeniHareket = malzemeHareketService.createMalzemeHareket(createDto);
        return ResponseEntity.ok(yeniHareket);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MalzemeHareketResponseDto> updateMalzemeHareket(@PathVariable Long id, @RequestBody MalzemeHareketUpdateDto updateDto) {
        return ResponseEntity.ok(malzemeHareketService.updateMalzemeHareket(id, updateDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMalzemeHareket(@PathVariable Long id) {
        malzemeHareketService.deleteMalzemeHareket(id);
        return ResponseEntity.noContent().build();
    }
    
}
