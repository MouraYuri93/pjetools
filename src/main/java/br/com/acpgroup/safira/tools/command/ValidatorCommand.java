package br.com.acpgroup.safira.tools.command;

import br.com.acpgroup.safira.tools.validator.ExcelProcessor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.sql.SQLException;

@Command(
        name="verificar",
        description = "Verificar se avisos do PJE estão no Sigad"
)
@Slf4j
public class ValidatorCommand implements Runnable{
    @Override
    public void run() {
        try {
            (new ExcelProcessor()).run();
        } catch (IOException e) {
           log.error(e.getMessage());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
