package co.ao.base.service.api;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AdminService extends BaseApiService {

    /**
     * Obtém os indicadores globais para o dashboard do administrador.
     * Inclui totais de parceiros, leads, tickets e as respetivas tendências (%) de crescimento.
     */
    public Map<String, Object> getOverview() {
        return get("/admin/overview", Map.class);
    }
}
