package br.com.acpgroup.safira.tools.validator;

import br.com.acpgroup.safira.tools.ConfigReader;
import br.com.acpgroup.safira.tools.model.PjeModel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class ExcelProcessor {

    public  void run() throws IOException, SQLException {
        File[] excelList = new File(ConfigReader.getWorkspacePath()).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xlsx");
            }
        });


        String dbUrl = ConfigReader.getDatabaseConfig();
        String dbUser = ConfigReader.getDatabaseUser();
        String dbPassword = ConfigReader.getDatabasePassword();

        Validator validator = new Validator(dbUrl, dbUser, dbPassword);

        for (File file : excelList) {
            ExcelReader reader = new ExcelReader();
            List<PjeModel> models = reader.readExcel(file);

            validator.validateModels(models);

            String originalFileName = file.getName();
            String descricao = originalFileName.substring(16);
            String data = originalFileName.substring(0, 8);
            String outputFileName = descricao;
            File outputFile = new File(ConfigReader.getWorkspacePath() +"/validado/" + outputFileName);
            ExcelWriter writer = new ExcelWriter();
            writer.writeExcel(outputFile, models, data);


            File processedFile = new File(file.getAbsolutePath() + ".proc");
            Files.move(file.toPath(), processedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        validator.close();
    }
}
