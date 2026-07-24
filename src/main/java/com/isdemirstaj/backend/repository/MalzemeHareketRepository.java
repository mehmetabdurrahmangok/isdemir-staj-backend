package com.isdemirstaj.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isdemirstaj.backend.entity.MalzemeHareketEntity;
import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MalzemeHareketRepository extends JpaRepository<MalzemeHareketEntity, Long> {
    @Query(value = "SELECT COALESCE(SUM(CASE WHEN hareket_turu IN ('GELEN', 'URETIM') THEN miktar ELSE -miktar END), 0) FROM MALZEME_HAREKET_TBL WHERE mlz_id = :malzemeId", nativeQuery = true)
    public BigDecimal hesaplaMevcutStok(@Param("malzemeId") Long malzemeId);

    List<MalzemeHareketEntity> findByMalzemeId(Long malzemeId);

    public List<MalzemeHareketEntity> findByHareketTuruAndHareketTarihiBetween(
        HareketTuruEnum hareketTuru,
        LocalDateTime starDate,
        LocalDateTime endDate
    );
    // bu fonksiyonun adından dolayı çalıştığı zaman SQL de şu sorgu çalışır
    // SELECT * FROM malzeme_hareketleri 
    // WHERE hareket_turu = ? 
    // AND hareket_tarihi BETWEEN ? AND ?;
}
