package co.ao.base.model;

import lombok.Data;
import java.util.List;

@Data
public class MaterialApoioCreateRequest {
    private String nome;
    private List<String> tiposParceiroPublicIds;
    private String tipoConteudo;
}
