package br.com.acpgroup.safira.tools.validator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import br.com.acpgroup.safira.tools.ConfigReader;
import br.com.acpgroup.safira.tools.MySQLDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExcelValidator {

    public void run() {
        File destinoPath = new File(ConfigReader.getWorkspacePath() + "/validado");
        if (!destinoPath.exists()) {
            destinoPath.mkdir();
        }

        File[] filesToProcess = destinoPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xlsx");
            }
        });

        for (File item : filesToProcess) {
            verificarArquivo(item);
        }
    }

    private void verificarArquivo(File arquivo) {
        String dbUrl = ConfigReader.getDatabaseConfig();
        String dbUser = ConfigReader.getDatabaseUser();
        String dbPassword = ConfigReader.getDatabasePassword();

        try (FileInputStream fis = new FileInputStream(arquivo.getPath());
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            MySQLDatabase database = new MySQLDatabase(dbUrl, dbUser, dbPassword);

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                log.info("Planilha: " + sheet.getSheetName());

                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }

                    Cell processoCell = row.getCell(1);
                    Cell avisoCell = row.getCell(2);

                    if ((processoCell != null && !processoCell.toString().isEmpty()) && avisoCell != null) {
                        String processo = processoCell.toString();
                        Long id = database.doesRecordExist(processo, (long) avisoCell.getNumericCellValue());

                        log.info("Processo: " + processo + "\tAviso: " + avisoCell.getNumericCellValue() + ": " + ((id == null) ? "Not Found" : id));

                        Cell resultCell = row.getCell(10);
                        if (resultCell == null) {
                            resultCell = row.createCell(10);
                        }
                        resultCell.setCellValue((id == null) ? "Not Found" : id.toString());
                    } else {
                        log.warn("Ignorando linha: " + row.getRowNum());
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                workbook.write(fos);
                log.info("Processamento Terminado: " + arquivo.getName());
            }

            database.close();
        } catch (IOException | SQLException e) {
            log.error("Erro ao processar o arquivo: " + arquivo.getName() + " - " + e.getMessage());
        }
    }
}
