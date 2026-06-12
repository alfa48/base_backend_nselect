package co.ao.base.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeadDTO {
    private static final Logger log = LoggerFactory.getLogger(LeadDTO.class);
    private String publicId;
    private String nome;
    private String email;
    private String telemovel;
    private String nif;
    private String estado;
    @JsonProperty("comprovantivoUrl")
    private String comprovativoUrl;
    private String pacotePublicId;
    private String pacoteNome;
    private Double pacotePreco;
    private String parceiroPublicId;
    private String parceiroNome;
    private String usuarioPublicId;
    private String usuarioNome;
    private String createdAt;
    private String updatedAt;
    private List<LeadNotaDTO> notas;

    public String getPublicId() { return publicId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelemovel() { return telemovel; }
    public String getNif() { return nif; }
    public String getEstado() { return estado; }
    public String getComprovativoUrl() { return comprovativoUrl; }
    public String getTelefone() { return telemovel; }
    public String getPacotePublicId() { return pacotePublicId; }
    public String getPacoteNome() { return pacoteNome; }
    public Double getPacotePreco() { return pacotePreco; }
    public String getParceiroPublicId() { return parceiroPublicId; }
    public String getParceiroNome() { return parceiroNome; }
    public String getUsuarioPublicId() { return usuarioPublicId; }
    public String getUsuarioNome() { return usuarioNome; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public LocalDateTime getCreatedAtDate() {
        if (createdAt == null) return null;
        try {
            // Tenta parse normal (sem offset)
            return LocalDateTime.parse(createdAt.length() > 19 ? createdAt.substring(0, 19) : createdAt);
        } catch (Exception e) {
            try {
                // Tenta parse com offset ou Z
                return ZonedDateTime.parse(createdAt).toLocalDateTime();
            } catch (Exception e2) {
                try {
                    // Tenta o formato angolano antigo
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    return LocalDateTime.parse(createdAt, formatter);
                } catch (Exception ex) {
                    log.error("DEBUG DATETIME: Nao foi possivel parsear createdAt -> {}", createdAt);
                    return null;
                }
            }
        }
    }
    public List<LeadNotaDTO> getNotas() { return notas; }

    @Data
    public static class LeadNotaDTO {
        private String publicId;
        private String nota;
        private String usuarioPublicId;
        private String usuarioNome;
        private String createdAt;
        private String updatedAt;

        public String getPublicId() { return publicId; }
        public String getNota() { return nota; }
        public String getUsuarioPublicId() { return usuarioPublicId; }
        public String getUsuarioNome() { return usuarioNome; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }

        public LocalDateTime getCreatedAtDate() {
            if (createdAt == null) return null;
            try {
                return LocalDateTime.parse(createdAt.length() > 19 ? createdAt.substring(0, 19) : createdAt);
            } catch (Exception e) {
                try {
                    return ZonedDateTime.parse(createdAt).toLocalDateTime();
                } catch (Exception e2) {
                    try {
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        return LocalDateTime.parse(createdAt, formatter);
                    } catch (Exception ex) {
                        log.error("DEBUG DATETIME: Nao foi possivel parsear nota createdAt -> {}", createdAt);
                        return null;
                    }
                }
            }
        }
    }
}
