package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.MalzemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MalzemeRepository extends JpaRepository<MalzemeEntity, Long> {
    // Malzeme sınıfı için repository arayüzü, JpaRepository'yi genişletir ve MalzemeEntity ile Long tipinde birincil anahtar kullanır.
    Optional<MalzemeEntity> findByMalzemeKodu(String malzemeKodu);
}
