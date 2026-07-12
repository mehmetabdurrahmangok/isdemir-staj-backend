package com.isdemirstaj.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.isdemirstaj.backend.entity.enums.HareketTuruEnum;

@Entity
@Getter
@Setter
@Table(name = "MALZEME_HAREKET_TBL")
public class MalzemeHareketEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "MLZ_ID", referencedColumnName = "ID")
    private MalzemeEntity malzeme;

    @Column(name = "TARIH")
    private LocalDateTime hareketTarihi;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "HAREKET_TURU")
    private HareketTuruEnum hareketTuru;

    @Column(name = "MIKTAR")
    private BigDecimal miktar;
}
