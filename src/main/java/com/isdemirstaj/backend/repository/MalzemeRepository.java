package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.MalzemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MalzemeRepository extends JpaRepository<MalzemeEntity, Long> {
    Optional<MalzemeEntity> findByMalzemeKodu(String malzemeKodu);
}
