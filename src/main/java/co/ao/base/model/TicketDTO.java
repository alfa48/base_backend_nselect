package co.ao.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private String publicId;
    private String conteudo;
    private String tipo;
    private String estado;
    private String publicadoPorPublicId;
    private String publicadoPorNome;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}
