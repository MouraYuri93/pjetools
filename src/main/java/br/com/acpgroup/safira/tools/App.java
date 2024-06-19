package br.com.acpgroup.safira.tools;

import static picocli.CommandLine.Command;

import br.com.acpgroup.safira.tools.command.ExtractorCommand;
import br.com.acpgroup.safira.tools.command.ValidatorCommand;
import picocli.CommandLine;

@Command(
        name= "pje-tools.jar",
        description = "Ajudar a idenditificar problemas de Importação",
        subcommands ={
                ExtractorCommand.class,
                ValidatorCommand.class
        }
)
public class App implements Runnable{
    public static void main(String[] args) {
        System.setProperty("log4j2.loggerContextFactory", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
