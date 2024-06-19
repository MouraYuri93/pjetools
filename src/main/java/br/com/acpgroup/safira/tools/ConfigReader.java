package br.com.acpgroup.safira.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        String propertiesPath = "config.properties";
        try (FileInputStream fis = new FileInputStream(propertiesPath)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de propriedades: " + e.getMessage());
            System.exit(1);
        }
    }

    // Método para obter a URL do banco de dados
    public static String getDatabaseConfig() {
        return properties.getProperty("db.config");
    }

    // Método para obter o nome do usuário do banco de dados
    public static String getDatabaseUser() {
        return properties.getProperty("db.user");
    }

    // Método para obter a senha do banco de dados
    public static String getDatabasePassword() {
        return properties.getProperty("db.password");
    }

    public static String getChromeDriverPath() {
        return properties.getProperty("tools.chromedriver.path");
    }
    public static String getChromePath() {
        return properties.getProperty("tools.chrome.path");
    }
    public static String getWorkspacePath() {
        return properties.getProperty("workspace.path");
    }
    public static String getPjeUrl() {
        return properties.getProperty("pje.url");
    }
}