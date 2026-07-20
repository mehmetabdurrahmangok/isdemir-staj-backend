package com.isdemirstaj.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@Subselect("SELECT * FROM VW_MALZEME_DETAY") // @Table YERİNE BUNU KULLANIYORUZ
public class MalzemeDetayViewEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "malzeme_kodu")
    private String malzemeKodu;

    @Column(name = "malzeme_adi")
    private String malzemeAdi;

    @Column(name = "mensei")
    private String mensei;

    @Column(name = "tur_id")
    private Long turId;

    @Column(name = "tur_adi")
    private String turAdi;

    @Column(name = "oper")
    private String oper;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}