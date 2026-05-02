package co.ao.base.model;

import lombok.Data;

@Data
public class LeadCreateRequest {
    private String nome;
    private String email;
    private String telemovel;
    private String nif;
    private String pacotePublicId;
    private String estado;
}
