package co.ao.base.model;

import lombok.Data;

@Data
public class ParceiroCreateRequest {
    private String nome;
    private String nif;
    private String iban;
    private String telefone;
    private String email;
    private String password;
    private String endereco;
    private String tipoParceiroPublicId;
    private String provinciaPublicId;
}
