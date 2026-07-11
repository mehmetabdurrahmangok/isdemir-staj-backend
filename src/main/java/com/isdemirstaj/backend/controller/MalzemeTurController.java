package com.isdemirstaj.backend.controller;

import com.isdemirstaj.backend.dto.MalzemeTurResponseDto;
import com.isdemirstaj.backend.service.MalzemeTurService;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin // bir porttan diğerine istek atabilmek için
@RestController // bu sınıfın bir controller olduğunu belirtir
@RequestMapping("/api/malzemeTurleri") // bu controllerin hangi endpoint ile ilişkilendirileceğini belirtir
public class MalzemeTurController {

    private final MalzemeTurService malzemeTurService; // MalzemeTurService sınıfının bir örneğini tutar
    // service fonksiyonları controller üzerinden çağırılır

    public MalzemeTurController(MalzemeTurService malzemeTurService) {
        this.malzemeTurService = malzemeTurService;
    } // constructor injection yöntemi ile MalzemeTurService sınıfının bir örneğini alır

    // Bütün malzeme türlerini listeleyen endpoint
    @GetMapping
    public List<MalzemeTurResponseDto> getMalzemeTurleri() {
        return malzemeTurService.getAllMalzemeTurleri();
    }
    
}
