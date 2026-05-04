package co.ao.base.service.api;

import co.ao.base.model.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminService extends BaseApiService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    /**
     * Obtém os indicadores globais para o dashboard do administrador.
     * Consome dados reais de parceiros e leads via paginação (size=1).
     */
    public Map<String, Object> getOverview() {
        java.util.HashMap<String, Object> overview = new java.util.HashMap<>();
        
        try {
            // Buscar total de parceiros
            PageResponse<?> partners = get("/admin/parceiros?tamanho=1", PageResponse.class);
            overview.put("totalParceiros", partners != null ? partners.getTotalElements() : 0L);
            
            // Buscar total de leads (Admin vê todos)
            PageResponse<?> leads = get("/leads/admin/todos?tamanho=1", PageResponse.class);
            overview.put("totalLeads", leads != null ? leads.getTotalElements() : 0L);
            
            // Mock de tickets e tendências (não disponíveis na API real via overview)
            overview.put("ticketsAbertos", 0);
            overview.put("totalParceirosTrend", 0.0);
            overview.put("totalLeadsTrend", 0.0);
            overview.put("ticketsAbertosTrend", 0.0);
            
        } catch (Exception e) {
            log.error("Erro ao construir overview admin: {}", e.getMessage());
            overview.put("totalParceiros", 0L);
            overview.put("totalLeads", 0L);
            overview.put("ticketsAbertos", 0);
        }
        
        return overview;
    }
}
