package com.isdemirstaj.backend.dto.malzeme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalzemeResponseDto { 
    private Long id;
    private String malzemeKodu;
    private String malzemeAdi;
    private Long malzemeTurId;
    private String malzemeTurAdi;
    private String mensei;
}