package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.MalzemeTurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// MalzemeTur sınıfı için repository arayüzü, JpaRepository'yi genişletir ve MalzemeTurEntity ile Long tipinde birincil anahtar kullanır.
public interface MalzemeTurRepository extends JpaRepository<MalzemeTurEntity, Long> {
    
}
