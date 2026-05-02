package co.ao.base.model;

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
    private LocalDateTime createdAt;
}
