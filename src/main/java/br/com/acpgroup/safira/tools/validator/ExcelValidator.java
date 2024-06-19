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

    public void run(){
        File destinoPath = new File(ConfigReader.getWorkspacePath() + "/validado");
        if(!destinoPath.exists()){
            destinoPath.mkdir();
        }

        File[] filesToProcess = destinoPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xlsx");
            }
        });

        for (File item : filesToProcess){
            verificarArquivo(item);
        }
    }

    private void verificarArquivo(File arquivo){

        String dbUrl = ConfigReader.getDatabaseConfig();
        String dbUser = ConfigReader.getDatabaseUser();
        String dbPassword = ConfigReader.getDatabasePassword();

        try (FileInputStream fis = new FileInputStream(arquivo.getPath());
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream("")) {

            // Conectar ao banco de dados MySQL
            MySQLDatabase database = new MySQLDatabase(dbUrl, dbUser, dbPassword);

            // Iterar sobre todas as planilhas no arquivo Excel
            for (int sheetIndex = 1; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                System.out.println("Planilha: " + sheet.getSheetName());

                // Começar da segunda linha (linha 1, pois a indexação é zero)
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;  // Pular linhas vazias
                    }

                    // Obter o valor dos critérios (ajustar conforme necessário)
                    Cell processoCell = row.getCell(0);  // Coluna do Processo
                    Cell avisoCell = row.getCell(1);  // Ajuste para a coluna correta do aviso

                    if ((processoCell != null && processoCell.toString() !=  "") &&  avisoCell != null){

                        String processo = (processoCell != null) ? processoCell.toString() : "";

                        Long id = database.doesRecordExist(processo, (long) avisoCell.getNumericCellValue());

                        log.info("Processo:"+processo + "\tAviso:" + avisoCell.getNumericCellValue() + ":" + ((id == null) ? "Not Found" : id));


                        Cell resultCell = row.getCell(9);
                        if (resultCell == null) {
                            resultCell = row.createCell(9);
                        }
                        resultCell.setCellValue((id == null) ? "Not Found" : id.toString());
                    }else {
                        log.warn("Ignorando linha: " + row.getRowNum());
                    }
                }
            }
            workbook.write(fos);
            database.close();
        } catch (IOException | SQLException e) {
            log.error(e.getMessage());
            return;
        }

        try {
            Files.move(Paths.get("tempExcelPath"), Paths.get("excelPath"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            log.info("Processamento Terminado");
        } catch (IOException e) {
            log.error("Erro ao substituir o arquivo original: " + e.getMessage());
        }

    }
}
