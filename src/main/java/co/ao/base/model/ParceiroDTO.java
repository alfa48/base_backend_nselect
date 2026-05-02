package co.ao.base.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParceiroDTO {
    private String publicId;
    private String nome;
    private String telefone;
    private String email;
    private String nif;
    private String iban;
    private String endereco;
    private String fotoUrl;
    private String documentoUrl;
    private boolean temAnexo;
    private String provinciaPublicId;
    private String provinciaNome;
    private String tipoParceiroPublicId;
    private String tipoParceiroNome;
    private String usuarioPublicId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getDocumentoUrl() {
        return documentoUrl;
    }

    public void setDocumentoUrl(String documentoUrl) {
        this.documentoUrl = documentoUrl;
    }
}
