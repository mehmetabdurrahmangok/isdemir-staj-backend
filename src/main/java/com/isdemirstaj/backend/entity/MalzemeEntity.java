package com.isdemirstaj.backend.entity;

import com.isdemirstaj.backend.entity.enums.MenseiEnum;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MALZEME_TANIM_TBL")
public class MalzemeEntity extends BaseEntity { // Malzeme tablosunu temsil eden entity sınıfı
    
    @ManyToOne
    @JoinColumn(name = "MALZEME_TUR_ID", referencedColumnName = "ID")
    private MalzemeTurEntity malzemeTur;

    @Column(name = "MALZEME_KODU", unique = true)
    private String malzemeKodu;

    @Column(name = "MALZEME_ADI")
    private String malzemeAdi;

    @Enumerated(EnumType.STRING)
    @Column(name = "MENSEI")
    private MenseiEnum mensei;
}
