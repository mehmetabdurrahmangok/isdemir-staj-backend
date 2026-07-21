package com.isdemirstaj.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseSetup {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSetup(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        String viewSql = """
            CREATE OR REPLACE VIEW VW_MALZEME_DETAY AS
            SELECT
                m.id AS id,
                m.malzeme_kodu AS malzeme_kodu,
                m.malzeme_adi AS malzeme_adi,
                m.mensei AS mensei,
                t.id AS tur_id,
                t.malzeme_turu AS tur_adi,
                m.oper AS oper,
                m.update_at AS update_at
            FROM MALZEME_TANIM_TBL m
            LEFT JOIN MALZEME_TURU_TANIM_TBL t ON m.malzeme_tur_id = t.id
        """;
        
        jdbcTemplate.execute(viewSql);
        System.out.println("✅ VW_MALZEME_DETAY görünümü veritabanında başarıyla güncellendi.");
    
                // 2. FUNCTION OLUŞTURMA KISMI (PACKAGE MANTIĞI İLE)
        String functionSql = """
            -- Önce Oracle'daki 'Package' mantığını taklit etmek için Şema oluşturuyoruz
            CREATE SCHEMA IF NOT EXISTS pkg_malzeme;
            
            -- Fonksiyonumuzu bu paketin (şemanın) içine koyuyoruz: pkg_malzeme.fn_guncel...
            CREATE OR REPLACE FUNCTION pkg_malzeme.fn_guncel_stok_hesapla(p_malzeme_id BIGINT)
            RETURNS NUMERIC AS $$
            DECLARE
                v_stok NUMERIC;
            BEGIN
                SELECT COALESCE(SUM(CASE 
                    WHEN HAREKET_TURU IN ('GELEN', 'URETIM') THEN MIKTAR 
                    WHEN HAREKET_TURU IN ('TUKETIM', 'SATIS', 'GIDEN') THEN -MIKTAR 
                    ELSE 0 
                END), 0) 
                INTO v_stok
                FROM MALZEME_HAREKET_TBL 
                WHERE MLZ_ID = p_malzeme_id;
            
                RETURN v_stok;
            END;
            $$ LANGUAGE plpgsql;
        """;
        jdbcTemplate.execute(functionSql);
        System.out.println("✅ fn_guncel_stok_hesapla() fonksiyonu pkg_malzeme paketi içine başarıyla eklendi!");

        String procedureSql = """
                CREATE OR REPLACE PROCEDURE pkg_malzeme.pr_malzeme_sil(p_malzeme_id
                BIGINT)
                LANGUAGE plpgsql
                AS $$
                BEGIN
                    -- gelen id değerine sahip malzemeyi siliyor
                    DELETE FROM MALZEME_TANIM_TBL WHERE id = p_malzeme_id;
                END;
                $$;
            """;
            jdbcTemplate.execute(procedureSql);
            System.out.println("✅ pr_malzeme_sil() prosedürü pkg_malzeme paketi içine başarıyla eklendi!");
    }
}