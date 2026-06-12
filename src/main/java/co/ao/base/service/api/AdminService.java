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
        
        // Valores por defeito para evitar SpelEvaluationException no Thymeleaf
        overview.put("totalParceiros", 0L);
        overview.put("totalParceirosTrend", 0.0);
        overview.put("totalLeads", 0L);
        overview.put("totalLeadsTrend", 0.0);
        overview.put("ticketsAbertos", 0L);
        overview.put("ticketsAbertosTrend", 0.0);
        overview.put("faturacaoTotal", 0.0);
        overview.put("faturacaoTotalTrend", 0.0);
        
        try {
            // Buscar total de parceiros
            PageResponse<?> partners = get("/admin/parceiros?tamanho=1", PageResponse.class);
            overview.put("totalParceiros", partners != null ? partners.getTotalElements() : 0L);
            
            // Buscar total de leads (Admin vê todos) e calcular faturação
            PageResponse<co.ao.base.model.LeadDTO> leads = get("/leads/admin/todos?tamanho=1000", new org.springframework.core.ParameterizedTypeReference<PageResponse<co.ao.base.model.LeadDTO>>() {});
            long totalLeads = 0L;
            long convertedLeads = 0L;
            double faturacaoConvertidos = 0.0;
            double faturacaoTotalBase = 0.0;
            
            if (leads != null && leads.getContent() != null) {
                totalLeads = leads.getTotalElements();
                for (co.ao.base.model.LeadDTO lead : leads.getContent()) {
                    double preco = lead.getPacotePreco() != null ? lead.getPacotePreco() : 0.0;
                    faturacaoTotalBase += preco;
                    if ("CONVERTIDO".equalsIgnoreCase(lead.getEstado())) {
                        convertedLeads++;
                        faturacaoConvertidos += preco;
                    }
                }
            }
            overview.put("totalLeads", totalLeads);
            
            double totalLeadsTrend = 0.0;
            if (totalLeads > 0) {
                totalLeadsTrend = ((double) convertedLeads / totalLeads) * 100.0;
            }
            overview.put("totalLeadsTrend", totalLeadsTrend);
            
            overview.put("faturacaoTotal", faturacaoConvertidos);
            double faturacaoTotalTrend = 0.0;
            if (faturacaoTotalBase > 0) {
                faturacaoTotalTrend = (faturacaoConvertidos / faturacaoTotalBase) * 100.0;
            }
            overview.put("faturacaoTotalTrend", faturacaoTotalTrend);
            
            // Buscar total de tickets
            PageResponse<co.ao.base.model.TicketDTO> tickets = get("/tickets/admin/todos?tamanho=1000", new org.springframework.core.ParameterizedTypeReference<PageResponse<co.ao.base.model.TicketDTO>>() {});
            long ticketsAbertos = 0L;
            long totalTickets = tickets != null ? tickets.getTotalElements() : 0L;
            if (tickets != null && tickets.getContent() != null) {
                ticketsAbertos = tickets.getContent().stream()
                    .filter(t -> "ABERTO".equalsIgnoreCase(t.getEstado()))
                    .count();
            }
            overview.put("ticketsAbertos", ticketsAbertos);
            overview.put("totalParceirosTrend", 0.0);
            
            double ticketsAbertosTrend = 0.0;
            if (totalTickets > 0) {
                ticketsAbertosTrend = ((double) ticketsAbertos / totalTickets) * 100.0;
            }
            overview.put("ticketsAbertosTrend", ticketsAbertosTrend);
            
        } catch (Exception e) {
            log.error("Erro ao construir overview admin: {}", e.getMessage());
        }
        
        return overview;
    }
}
