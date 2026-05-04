package co.ao.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LeadDTO {
    private String publicId;
    private String nome;
    private String email;
    private String telemovel;
    private String nif;
    private String estado;
    private String comprovantivoUrl;
    private String pacotePublicId;
    private String pacoteNome;
    private Double pacotePreco;
    private String parceiroPublicId;
    private String parceiroNome;
    private String usuarioPublicId;
    private String usuarioNome;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private List<LeadNotaDTO> notas;

    public String getPublicId() { return publicId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelemovel() { return telemovel; }
    public String getNif() { return nif; }
    public String getEstado() { return estado; }
    public String getComprovantivoUrl() { return comprovantivoUrl; }
    public String getPacotePublicId() { return pacotePublicId; }
    public String getPacoteNome() { return pacoteNome; }
    public Double getPacotePreco() { return pacotePreco; }
    public String getParceiroPublicId() { return parceiroPublicId; }
    public String getParceiroNome() { return parceiroNome; }
    public String getUsuarioPublicId() { return usuarioPublicId; }
    public String getUsuarioNome() { return usuarioNome; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<LeadNotaDTO> getNotas() { return notas; }

    @Data
    public static class LeadNotaDTO {
        private String publicId;
        private String nota;
        private String usuarioPublicId;
        private String usuarioNome;
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        private LocalDateTime updatedAt;

        public String getPublicId() { return publicId; }
        public String getNota() { return nota; }
        public String getUsuarioPublicId() { return usuarioPublicId; }
        public String getUsuarioNome() { return usuarioNome; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
    }
}
