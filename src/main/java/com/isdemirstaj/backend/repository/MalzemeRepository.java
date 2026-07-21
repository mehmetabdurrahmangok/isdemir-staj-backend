package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.MalzemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MalzemeRepository extends JpaRepository<MalzemeEntity, Long> {
    // Malzeme sınıfı için repository arayüzü, JpaRepository'yi genişletir ve MalzemeEntity ile Long tipinde birincil anahtar kullanır.
    Optional<MalzemeEntity> findByMalzemeKodu(String malzemeKodu);

    @Modifying // sorgunun select harici bir şey olabileceğini belirtir.
    @Transactional // işlemi atomik hale getirir.
    @Query(value = "CALL pkg_malzeme.pr_malzeme_sil(:malzemeId)", nativeQuery = true) // Sql sorgusu yazmak için
    void prosedurIleSil(@Param("malzemeId") Long malzemeId);
}
