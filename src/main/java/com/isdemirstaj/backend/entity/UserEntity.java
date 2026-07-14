package com.isdemirstaj.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "KULLANICI_TBL")
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // Tarihlerin otomatik doldurulması için
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Veritabanı otomatik artırsın
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "SIFRE_HASH", nullable = false)
    private String password;

    @Column(name = "AD_SOYAD", nullable = false)
    private String adSoyad;

    @Column(name = "AKTIF_MI", nullable = false)
    private Boolean aktifMi = true;

    @Column(name = "ROL", nullable = false)
    private String rol;

    @Column(name = "OPER")
    private String oper;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;
}