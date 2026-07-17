package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.report.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExcelExportService {
    
        public byte[] exportPivotToExcel(ReportResponseDTO report, String raporBasligi) throws IOException {

            // Yeni bir çalışma sayfası oluştur (excel)
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Tarihe Göre Malzeme Hareket Raporu");

                // Ekranı Ayarlama

                // Başlık stili: Mavi Arka Plan, Beyaz ve Kalın yazı
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex()); 
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setBorderBottom(BorderStyle.THIN);

                // tür toplam: Sarı Arka Plan, Kalın Yazı
                CellStyle yellowTotalStyle = workbook.createCellStyle();
                yellowTotalStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                yellowTotalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                yellowTotalStyle.setFont(boldFont);

                // Genel Toplam: Turuncu Arka Plan, Kalın Yazı
                CellStyle orangeTotalStyle = workbook.createCellStyle();
                orangeTotalStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                orangeTotalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                orangeTotalStyle.setFont(boldFont);

                // Standart Hücre
                CellStyle normalStyle = workbook.createCellStyle();
                int rowIdx = 0;

                Row titleRow = sheet.createRow(rowIdx++); // en üste başlık ekleme
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(raporBasligi);

                // Ana başlığın kalın ve büyük görünmesi için basit bir stil
                CellStyle titleStyle = workbook.createCellStyle();
                Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 14); // Yazıyı biraz büyüttük
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER); // Ortala
                titleCell.setCellStyle(titleStyle);

                // Başlığı yayma işlemi sütun birleştirme
                if (report.getColumns().size() > 0) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0,0,0,
                    report.getColumns().size()));
                }
                

                // Başlık Yazdırma (HEADER)
                Row headerRow = sheet.createRow(rowIdx++);

                // En sol üst köşe: Tarih
                Cell tarihHeaderCell = headerRow.createCell(0);
                tarihHeaderCell.setCellValue("Tarih");
                tarihHeaderCell.setCellStyle(headerStyle);

                List<ReportColumnDTO> columns = report.getColumns();
                for (int i = 0; i < columns.size(); i++) {
                    Cell cell = headerRow.createCell(i + 1);
                    cell.setCellValue(columns.get(i).getHeaderName());
                    cell.setCellStyle(headerStyle);
                }

                // veri satırlarını yazdırma
                for (ReportRowDTO dataRow : report.getRows()) {
                    Row row = sheet.createRow(rowIdx++);
                
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(dataRow.getTarih());
                    dateCell.setCellStyle(normalStyle);
                    for (int i = 0; i < columns.size(); i++) {
                        ReportColumnDTO col = columns.get(i);
                        Cell cell = row.createCell(i + 1);
                    
                        BigDecimal val = dataRow.getValues().getOrDefault(col.getHeaderName(), BigDecimal.ZERO);
                    
                        // Eğer değer 0 ise ekranda '-' gösterelim (Görüntünüzdeki gibi)
                        if (val.compareTo(BigDecimal.ZERO) == 0) {
                            cell.setCellValue("-");
                        } else {
                            cell.setCellValue(val.doubleValue());
                        }
                        // Sütun "TÜR TOPLAM" ise arka planı Sarı yap
                        if (col.isTotalColumn()) {
                            cell.setCellStyle(yellowTotalStyle);
                        } else {
                            cell.setCellStyle(normalStyle);
                        }
                    }
                }

                // genel toplam satırını yazdırma
                Row grandTotalRow = sheet.createRow(rowIdx);
                Cell totalTextCell = grandTotalRow.createCell(0);
                totalTextCell.setCellValue("GENEL TOPLAM");
                totalTextCell.setCellStyle(orangeTotalStyle);
                for (int i = 0; i < columns.size(); i++) {
                    ReportColumnDTO col = columns.get(i);
                    Cell cell = grandTotalRow.createCell(i + 1);
                
                    BigDecimal val = report.getRowTotal().getOrDefault(col.getHeaderName(), BigDecimal.ZERO);
                
                    if (val.compareTo(BigDecimal.ZERO) == 0) {
                        cell.setCellValue("-");
                    } else {
                        cell.setCellValue(val.doubleValue());
                    }
                
                    cell.setCellStyle(orangeTotalStyle); // Alt satır komple turuncu olacak
                }
                // Sütunların genişliğini içindeki yazıya göre otomatik ayarla
                for (int i = 0; i <= columns.size(); i++) {
                    sheet.autoSizeColumn(i);
                }
                // Dosyayı byte dizisine (binary format) çevirip dışarıya aktar
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                return out.toByteArray();
            }
        }
}
