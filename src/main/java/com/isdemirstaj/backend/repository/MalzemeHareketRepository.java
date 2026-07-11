package com.isdemirstaj.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isdemirstaj.backend.entity.MalzemeHareketEntity;

public interface MalzemeHareketRepository extends JpaRepository<MalzemeHareketEntity, Long> {
    
}
