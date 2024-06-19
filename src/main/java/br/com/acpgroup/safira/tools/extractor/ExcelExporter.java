package br.com.acpgroup.safira.tools.extractor;

import br.com.acpgroup.safira.tools.ConfigReader;
import br.com.acpgroup.safira.tools.model.PjeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

@Slf4j
public class ExcelExporter {

    private static String[] columns = {
            "Processo",
            "NumeroDocumento",
            "TipoDocumento",
            "MeioComunicacao",
            "Destinatario",
            "Prazo",
            "DataCriacao",
            "DataLimiteCiencia",
            "DataCiencia",
            "IdSigad"
    };

    public static void run(Set<PjeModel> data, String title){
        File destinoPath = new File(ConfigReader.getWorkspacePath());
        if(!destinoPath.exists()){
            destinoPath.mkdir();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(title.substring(0,8));

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        CellStyle dateCellStyle  = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        int rowNum = 1;
        for (PjeModel pje : data) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(pje.getProcesso());
            row.createCell(1, CellType.NUMERIC).setCellValue(pje.getNumeroDocumento());
            row.createCell(2).setCellValue(pje.getTipoDocumento());
            row.createCell(3).setCellValue(pje.getMeioComunicacao());
            row.createCell(4).setCellValue(pje.getDestinatario());
            row.createCell(5).setCellValue(pje.getPrazo());

            if(pje.getDataCriacao() != null) {
                Cell cell = row.createCell(6);
                cell.setCellStyle(dateCellStyle);
                cell.setCellValue(pje.getDataCriacao());
            }

            if(pje.getDataLimiteCiencia() != null) {
                Cell cell = row.createCell(7);
                cell.setCellStyle(dateCellStyle);
                cell.setCellValue(pje.getDataLimiteCiencia());
            }

            if(pje.getDataCiencia() != null) {
                Cell cell = row.createCell(8);
                cell.setCellStyle(dateCellStyle);
                cell.setCellValue(pje.getDataCiencia());
            }

            row.createCell(9, CellType.NUMERIC).setCellValue("");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(ConfigReader.getWorkspacePath() + "/" + title+".xlsx")) {
            workbook.write(fileOut);
            log.info("Created File:" + title);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        try {
            workbook.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}