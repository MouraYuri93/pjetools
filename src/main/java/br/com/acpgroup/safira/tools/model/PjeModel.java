package br.com.acpgroup.safira.tools.model;

import lombok.Data;

import java.util.Date;

@Data
public class PjeModel {
    private String destinatario;
    private String tipoDocumento;
    private Long numeroDocumento;
    private Long idSigad;
    private String meioComunicacao;
    private Date dataCriacao;
    private Date dataLimiteCiencia;
    private Date dataCiencia;
    private String processo;
    private String prazo;

}