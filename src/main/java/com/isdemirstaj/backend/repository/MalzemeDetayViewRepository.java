package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.MalzemeDetayViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MalzemeDetayViewRepository extends JpaRepository<MalzemeDetayViewEntity, Long> {
    
    // Detay endpoint'in "koda göre" arama yaptığı için bu metodu ekledik
    Optional<MalzemeDetayViewEntity> findByMalzemeKodu(String malzemeKodu);
}