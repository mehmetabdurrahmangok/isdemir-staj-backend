package com.isdemirstaj.backend.dto.malzemeTur;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalzemeTurResponseDto {
    private Long id;
    private String malzemeTurAdi;
    private String oper;
    private LocalDateTime updatedAt;
}