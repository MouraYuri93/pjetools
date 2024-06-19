package br.com.acpgroup.safira.tools.extractor;

import br.com.acpgroup.safira.tools.ConfigReader;
import br.com.acpgroup.safira.tools.model.PjeModel;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PjeExtractor {

    //chave é o nome da pagina
    Map<String,Set<PjeModel>> dbTemp = new HashMap<>();
    Pattern regexTipoDocumento;
    Pattern regexProcesso;
    Pattern regexDataCiencia;
    String  dtRun;

    public PjeExtractor() {
        regexTipoDocumento = Pattern.compile("(.*)\\s*\\((\\d+)\\)");
        regexProcesso= Pattern.compile(".*\\s(\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4})");
        regexDataCiencia = Pattern.compile(".*(\\d{2}\\/\\d{2}\\/\\d{4}\\s\\d{2}:\\d{2})");

        dtRun = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    public void run(){
        log.info("webdriver.chrome.driver", ConfigReader.getChromeDriverPath());
        String chromeBinaryPath = ConfigReader.getChromePath();

        ChromeOptions options = new ChromeOptions();
        if(! chromeBinaryPath.isEmpty()){
            options.setBinary(chromeBinaryPath);
            log.info("Utilizando o Chrome em: " + chromeBinaryPath);
        }else{
            log.info("Utilizando o Chrome Default");
        }

        WebDriver nav = null;
        try {
            nav = new ChromeDriver();
        }catch (SessionNotCreatedException ex){
            log.error(ex.getMessage());
            System.exit(1);
        }
        nav.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        nav.get(ConfigReader.getPjeUrl());

        //Navagação
        boolean execute = true;
        while (execute){
            System.out.println("Navegue ate a pagina de captura de avisos que deseja extrair");
            awaitManualContinue();

            String titulo01 = nav.findElement(By.xpath("//*[@id=\"formExpedientes:Filtros\"]/div/div[1]/h6")).getText();
            String outputFile =  dtRun+ "_" + titulo01.replaceAll(" ", "-").replaceAll("\\|","-").replaceAll("ª","").replaceAll("---","-");

            Set<PjeModel> pjeData;
            if(dbTemp.get(outputFile)== null){
                pjeData = new HashSet<PjeModel>();
                dbTemp.put(outputFile, pjeData);
            }else{
                pjeData = dbTemp.get(outputFile);
            }
            WebElement tabela = nav.findElement(By.id("formExpedientes:tbExpedientes:tb"));
            List<WebElement> trs = tabela.findElements(By.tagName("tr"));
                for(WebElement tr: trs){
                    PjeModel rw = new PjeModel();
                    List<WebElement> tds = tr.findElements(By.tagName("td"));

                    rw.setDestinatario(tds.get(1).findElement(By.xpath(".//div[1]/div[1]/div[1]/span")).getText());

                    String meioComunicacao = tds.get(1).findElement(By.xpath(".//div[1]/div[1]/div[3]/span")).getText();
                    rw.setMeioComunicacao(removeDateAfter(meioComunicacao));
                    rw.setDataCriacao(obterDataAfter(meioComunicacao));

                    String tipoDocumento = tds.get(1).findElement(By.xpath(".//div[1]/div[1]/div[2]/span")).getText();
                    Matcher matcherTipoDocumento = regexTipoDocumento.matcher(tipoDocumento);
                    if (matcherTipoDocumento.find()){
                        rw.setTipoDocumento(matcherTipoDocumento.group(1));
                        rw.setNumeroDocumento(Long.valueOf(matcherTipoDocumento.group(2)));
                    }else{
                        log.warn("tipoDocumento: " + tipoDocumento + " nao pode ser carregado");
                    }

                    rw.setPrazo(tds.get(1).findElement(By.xpath(".//div[1]/div[1]/div[4]")).getText().replaceAll("Prazo:",""));
                    rw.setDataLimiteCiencia(obterDataAfter(tds.get(1).findElement(By.xpath(".//div[1]/div[1]/div[8]")).getText().replaceAll("Data limite prevista para ciência: ","")));
                    String processo = tds.get(1).findElement(By.xpath(".//div[1]/div[2]/div/div[1]/a")).getText();
                    Matcher matcherProcesso = regexProcesso.matcher(processo);
                    if (matcherProcesso.find()){
                        rw.setProcesso(matcherProcesso.group(1));
                    }else{
                        log.warn("Processo: " + processo + " nao pode ser carregado");
                    }
                    log.info(rw.toString());
                    pjeData.add(rw);
                }
            execute = awaitManualDecision();
        }

        dbTemp.forEach((fileName, data) -> ExcelExporter.run(data, fileName));
    }

    private static void awaitManualContinue() {
        System.out.println("Pressione 'C' e Enter para continuar...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();
            while (!"C".equalsIgnoreCase(input)) {
                System.out.println("Entrada inválida. Pressione 'C' e Enter para continuar...");
                input = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean awaitManualDecision() {
        System.out.println("Pressione 'R' e Enter para repetir a rotina, ou 'Q' e Enter para sair...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();
            while (!"R".equalsIgnoreCase(input) && !"Q".equalsIgnoreCase(input)) {
                System.out.println("Entrada inválida. Pressione 'R' e Enter para repetir a rotina, ou 'Q' e Enter para sair...");
                input = reader.readLine();
            }
            if ("Q".equalsIgnoreCase(input)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private String removeDateAfter(String value){
        int index = value.indexOf("(");
        return (index != -1) ? value.substring(0, index).trim() : value;
    }

    private Date obterDataAfter(String value){
        Matcher matcherData = regexDataCiencia.matcher(value);
        if (matcherData.find()){
            try {
                return (new SimpleDateFormat("dd/MM/yyyy HH:mm")).parse(matcherData.group(1));
            } catch (ParseException e) {
                log.error( e.getMessage());
                return null;
            }
        }else{
            log.warn("Data : " + value + " nao pode ser carregado");
            return null;
        }
    }
}

