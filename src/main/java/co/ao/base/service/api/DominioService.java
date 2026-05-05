package co.ao.base.service.api;

import co.ao.base.model.DominioDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DominioService extends BaseApiService {

    public List<DominioDTO> listarProvincias() {
        return get("/provincias", new ParameterizedTypeReference<List<DominioDTO>>() {});
    }

    public List<DominioDTO> listarTiposParceiro() {
        return get("/tipos-parceiro", new ParameterizedTypeReference<List<DominioDTO>>() {});
    }

    public List<co.ao.base.model.DominioDTO> listarPacotes() {
        return get("/pacotes", new ParameterizedTypeReference<List<co.ao.base.model.DominioDTO>>() {});
    }

    public List<java.util.Map<String, Object>> listarMeses() {
        return java.util.List.of(
            java.util.Map.of("id", 1, "nome", "Janeiro"),
            java.util.Map.of("id", 2, "nome", "Fevereiro"),
            java.util.Map.of("id", 3, "nome", "Março"),
            java.util.Map.of("id", 4, "nome", "Abril"),
            java.util.Map.of("id", 5, "nome", "Maio"),
            java.util.Map.of("id", 6, "nome", "Junho"),
            java.util.Map.of("id", 7, "nome", "Julho"),
            java.util.Map.of("id", 8, "nome", "Agosto"),
            java.util.Map.of("id", 9, "nome", "Setembro"),
            java.util.Map.of("id", 10, "nome", "Outubro"),
            java.util.Map.of("id", 11, "nome", "Novembro"),
            java.util.Map.of("id", 12, "nome", "Dezembro")
        );
    }
}
