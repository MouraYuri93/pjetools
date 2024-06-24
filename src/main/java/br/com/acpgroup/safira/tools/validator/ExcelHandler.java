package br.com.acpgroup.safira.tools.validator;

import br.com.acpgroup.safira.tools.ConfigReader;
import br.com.acpgroup.safira.tools.model.PjeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public class ExcelHandler {

    private static final String[] columns = {
            "Grupo", "Processo", "NumeroDocumento", "IdSigad", "TipoDocumento", "MeioComunicacao",
            "Destinatario", "DataCriacao", "DataLimiteCiencia", "DataCiencia", "Prazo"
    };

    public void writeExcel(String comarca, List<PjeModel> models, String data) throws IOException {
        String filePath = ConfigReader.getWorkspacePath() + "/" + comarca + ".xlsx";
        File file = new File(filePath);

        Workbook workbook;
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        Sheet sheet = workbook.getSheet(data);
        if (sheet == null) {
            sheet = workbook.createSheet(data);
        }

        // Verificar se o cabeçalho já existe
        if (sheet.getLastRowNum() == 0) {
            createHeaderRow(workbook, sheet);
        }

        int rowIndex = sheet.getLastRowNum() + 1;
        CellStyle dateCellStyle = createDateCellStyle(workbook);

        for (PjeModel model : models) {
            Row row = sheet.createRow(rowIndex++);
            String grupo = "";
            populateRow(row, model, grupo, dateCellStyle);
            log.info(model.toString());
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            log.info("Arquivo salvo: " + filePath);
        }

        workbook.close();
    }

    public void exportToExcel(Set<PjeModel> models, String fileName) throws IOException {
        String filePath = ConfigReader.getWorkspacePath() + "/" + fileName + ".xlsx";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dados"); //colocar data do dia da consulta

        createHeaderRow(workbook, sheet);

        int rowIndex = 1;
        CellStyle dateCellStyle = createDateCellStyle(workbook);

        for (PjeModel model : models) {
            Row row = sheet.createRow(rowIndex++);
            populateRow(row, model, "", dateCellStyle); // Assume grupo vazio para exportação direta
            log.info(model.toString());
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            log.info("Arquivo salvo: " + filePath);
        }

        workbook.close();
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
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
    }

    private CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
        return dateCellStyle;
    }

    private void populateRow(Row row, PjeModel model, String grupo, CellStyle dateCellStyle) {
        row.createCell(0).setCellValue(grupo);
        row.createCell(1).setCellValue(model.getProcesso());
        row.createCell(2).setCellValue(model.getNumeroDocumento());
        row.createCell(4).setCellValue(model.getTipoDocumento());
        row.createCell(5).setCellValue(model.getMeioComunicacao());
        row.createCell(6).setCellValue(model.getDestinatario());
        row.createCell(10).setCellValue(model.getPrazo());

        if (model.getDataCriacao() != null) {
            Cell cell = row.createCell(7);
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue(model.getDataCriacao());
        }

        if (model.getDataLimiteCiencia() != null) {
            Cell cell = row.createCell(8);
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue(model.getDataLimiteCiencia());
        }

        if (model.getDataCiencia() != null) {
            Cell cell = row.createCell(9);
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue(model.getDataCiencia());
        }

        if (model.getIdSigad() != null) {
            row.createCell(3).setCellValue(model.getIdSigad());
        }
    }
}
