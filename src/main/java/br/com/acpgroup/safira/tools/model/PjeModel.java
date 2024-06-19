package br.com.acpgroup.safira.tools.model;

import lombok.Data;

import java.util.Date;

@Data
public class PjeModel {
    private String destinatario;
    private String tipoDocumento;
    private Long numeroDocumento;
    private String meioComunicacao;
    private String prazo;
    private Date dataCriacao;
    private Date dataLimiteCiencia;
    private Date dataCiencia;
    private String processo;
    private Long idSigad;
}