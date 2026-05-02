package co.ao.base.model;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
