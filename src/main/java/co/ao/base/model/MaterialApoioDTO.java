package co.ao.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaterialApoioDTO {
    private String publicId;
    private String nome;
    private String arquivoUrl;
    private String tipoConteudo;
    private String tagPromocional;
    private String tagEducativo;
    private List<String> tiposParceiroPublicIds;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
