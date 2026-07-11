package com.isdemirstaj.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MALZEME_TURU_TANIM_TBL")
@Getter
@Setter
public class MalzemeTurEntity extends BaseEntity { // MalzemeTur tablosunu temsil eden entity sınıfı
    
    @Column(name = "MALZEME_TURU", nullable = false, unique = true)
    private String malzemeTurAdi;
}
