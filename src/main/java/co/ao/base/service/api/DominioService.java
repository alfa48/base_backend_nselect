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

    public List<DominioDTO> listarPacotes() {
        return get("/pacotes", new ParameterizedTypeReference<List<DominioDTO>>() {});
    }
}
