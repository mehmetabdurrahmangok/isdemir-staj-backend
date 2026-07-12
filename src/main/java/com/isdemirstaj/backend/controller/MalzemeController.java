package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.malzeme.MalzemeCreateDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeDetayResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeResponseDto;
import com.isdemirstaj.backend.dto.malzeme.MalzemeUpdateDto;
import com.isdemirstaj.backend.service.MalzemeService;

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
    // yeni malzeme eklemek için olan endpoint
    @PostMapping("/create")
    public MalzemeResponseDto createMalzeme(@RequestBody MalzemeCreateDto malzemeCreateDto) {
        return malzemeService.createMalzeme(malzemeCreateDto);
    }

    // Malzeme güncellemek için endpoint
    @PutMapping("/update/{id}")
    public ResponseEntity<MalzemeResponseDto> updateMalzeme(@PathVariable Long id, @RequestBody MalzemeUpdateDto updateDto) {
        MalzemeResponseDto guncellenenMalzeme = malzemeService.updateMalzeme(id, updateDto);
        return ResponseEntity.ok(guncellenenMalzeme);
    }

    // malzeme silmek için endpoint
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMalzeme(@PathVariable Long id) {
        malzemeService.deleteMalzeme(id);
        return ResponseEntity.noContent().build(); // 204 No Content döner (Başarılı ama içerik yok)
    }

    // koda göre malzeme çekmek için endpoint
    @GetMapping("/detay/{malzemeKodu}")
    public ResponseEntity<MalzemeDetayResponseDto> getMalzemeDetayByKodu(@PathVariable String malzemeKodu) {
        MalzemeDetayResponseDto detay = malzemeService.getMalzemeDetayByKodu(malzemeKodu);
        return ResponseEntity.ok(detay);
    }
}
