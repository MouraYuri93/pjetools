package br.com.acpgroup.safira.tools.command;

import br.com.acpgroup.safira.tools.extractor.PjeExtractor;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name="extrair",
        description = "Obter os avisos do PJE de forma Hibrida navegação manual, com extração automatizada"
)
public class ExtractorCommand implements Runnable{
    @Override
    public void run() {
        (new PjeExtractor()).run();
    }
}
