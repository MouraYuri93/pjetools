package br.com.acpgroup.safira.tools.validator;

import br.com.acpgroup.safira.tools.model.PjeModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public List<PjeModel> readExcel(File file) throws IOException {
        List<PjeModel> models = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                PjeModel model = new PjeModel();
                model.setGrupo(row.getCell(0).toString()); //verificar se a sintaxe está correta
                model.setProcesso(row.getCell(1).toString());
                model.setNumeroDocumento((long)row.getCell(2).getNumericCellValue());
                model.setTipoDocumento(row.getCell(4).toString());
                model.setMeioComunicacao(row.getCell(5).toString());
                model.setDestinatario(row.getCell(6).toString());
                model.setPrazo(row.getCell(10).toString());
                model.setDataCriacao(row.getCell(7) != null? row.getCell(7).getDateCellValue() : null);
                model.setDataLimiteCiencia(row.getCell(8)!= null? row.getCell(8).getDateCellValue(): null);
                model.setDataCiencia(row.getCell(9)!= null?row.getCell(9).getDateCellValue(): null);

                models.add(model);
            }
        }

        return models;
    }
}
