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

    public void run() throws IOException, SQLException {
        File[] excelList = new File(ConfigReader.getWorkspacePath()).listFiles(new FilenameFilter() {
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
            String comarca = originalFileName.split("_")[1].split("-")[0]; // Extraindo a comarca do nome do arquivo
            String grupo = originalFileName.split("-")[1]; // Extraindo o nome do grupo do nome do arquivo

            String data = originalFileName.substring(0, 8);
            ExcelWriter writer = new ExcelWriter();
            writer.writeExcel(comarca, grupo, models, data);

            File processedFile = new File(file.getAbsolutePath() + ".proc");
            Files.move(file.toPath(), processedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        validator.close();
    }
}
