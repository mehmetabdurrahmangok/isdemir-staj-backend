package com.isdemirstaj.backend.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity { // BaseEntity adında bir abstract sınıf tanımlar ve diğer entity sınıflarının temelini oluşturur

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_sequence")
    @SequenceGenerator(name = "base_sequence", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    private Long id;

    @Column(name = "OPER")
    private String oper;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

}
