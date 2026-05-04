package co.ao.base.controller;

import co.ao.base.service.api.LeadService;
import co.ao.base.service.api.MaterialApoioService;
import co.ao.base.service.api.ParceiroService;
import co.ao.base.service.api.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/parceiro")
public class ParceiroViewController {
    private static final Logger log = LoggerFactory.getLogger(ParceiroViewController.class);

    @Autowired
    private LeadService leadService;

    @Autowired
    private MaterialApoioService materialApoioService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ParceiroService parceiroService;

    @Autowired
    private co.ao.base.service.api.DominioService dominioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> overview = new HashMap<>();
        // Valores padrão para evitar erros no Thymeleaf (SpringEL)
        overview.put("totalLeads", 0L);
        overview.put("faturacaoTotal", 0.0);
        overview.put("totalLeadsTrend", 0.0);
        overview.put("faturacaoTotalTrend", 0.0);

        try {
            Map<String, Object> apiData = parceiroService.getOverview();
            if (apiData != null) {
                if (apiData.containsKey("totalLeads")) overview.put("totalLeads", apiData.get("totalLeads"));
                // Mapear totalFaturacao (API) para faturacaoTotal (Template)
                if (apiData.containsKey("totalFaturacao")) overview.put("faturacaoTotal", apiData.get("totalFaturacao"));
                // As tendências não existem na Libera API atual, mantemos os padrões 0.0
            }
        } catch (Exception e) {
            log.error("Erro ao obter overview do parceiro: {}", e.getMessage());
        }
        
        model.addAttribute("overview", overview);
        try {
            model.addAttribute("recentLeads", leadService.listarLeads(0, 5, null, null, null));
        } catch (Exception e) {
            model.addAttribute("recentLeads", new co.ao.base.model.PageResponse<>());
        }
        return "parceiro/dashboard-parceiros---parceiro";
    }

    @GetMapping("/leads")
    public String listarLeads(@RequestParam(defaultValue = "0") int pagina,
                              @RequestParam(defaultValue = "10") int tamanho,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String dataInicial,
                              @RequestParam(required = false) String dataFinal,
                              Model model) {
        try {
            model.addAttribute("leads", leadService.listarLeads(pagina, tamanho, estado, dataInicial, dataFinal));
        } catch (Exception e) {
            log.error("Erro ao listar leads do parceiro: {}", e.getMessage());
            model.addAttribute("leads", new co.ao.base.model.PageResponse<>());
        }
        return "parceiro/leads---parceiro/leads-main---parceiro";
    }

    @GetMapping("/leads/novo")
    public String novoLead(Model model) {
        try {
            model.addAttribute("pacotes", dominioService.listarPacotes());
        } catch (Exception e) {
            model.addAttribute("pacotes", java.util.Collections.emptyList());
        }
        return "parceiro/leads---parceiro/novo-lead---parceiro";
    }

    @GetMapping("/leads/editar/{id}")
    public String editarLead(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
            model.addAttribute("pacotes", dominioService.listarPacotes());
        } catch (Exception e) {
            log.error("Erro ao carregar lead para edição: {}", e.getMessage());
            return "redirect:/parceiro/leads?error=Erro ao carregar dados";
        }
        return "parceiro/leads---parceiro/editar-lead---parceiro";
    }

    @GetMapping("/leads/{id}")
    public String verLead(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
        } catch (Exception e) {
            log.error("Erro ao ver lead do parceiro: {}", e.getMessage());
            return "redirect:/parceiro/leads?error=Lead não encontrado";
        }
        return "parceiro/leads---parceiro/lead-individual---parceiro";
    }

    @GetMapping("/materiais")
    public String listarMateriais(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 Model model) {
        try {
            model.addAttribute("materiais", materialApoioService.listarMateriais(pagina, tamanho, nome, tipo));
        } catch (Exception e) {
            log.error("Erro ao listar materiais do parceiro: {}", e.getMessage());
            model.addAttribute("materiais", new co.ao.base.model.PageResponse<>());
        }
        return "parceiro/material-de-apoio---parceiro/material-de-apoio---parceiro";
    }

    @GetMapping("/materiais/{id}")
    public String verMaterial(@PathVariable String id, Model model) {
        return "parceiro/material-de-apoio---parceiro/material-individual---parceiro";
    }

    @GetMapping("/tickets")
    public String listarTickets(@RequestParam(defaultValue = "0") int pagina,
                               @RequestParam(defaultValue = "10") int tamanho,
                               Model model) {
        try {
            model.addAttribute("tickets", ticketService.listarTickets(pagina, tamanho));
        } catch (Exception e) {
            log.error("Erro ao listar tickets do parceiro: {}", e.getMessage());
            model.addAttribute("tickets", new co.ao.base.model.PageResponse<>());
        }
        return "parceiro/tickets/tickets-main---parceiro";
    }

    @GetMapping("/tickets/novo")
    public String novoTicket(Model model) {
        return "parceiro/tickets/criar-ticket---parceiro";
    }

    @GetMapping("/tickets/editar/{id}")
    public String editarTicket(@PathVariable String id, Model model) {
        model.addAttribute("ticket", ticketService.buscarTicket(id));
        return "parceiro/tickets/editar-ticket---parceiro";
    }

    @GetMapping("/tickets/{id}")
    public String verTicket(@PathVariable String id, Model model) {
        try {
            model.addAttribute("ticket", ticketService.buscarTicket(id));
        } catch (Exception e) {
            log.error("Erro ao ver ticket do parceiro: {}", e.getMessage());
            return "redirect:/parceiro/tickets?error=Ticket não encontrado";
        }
        return "parceiro/tickets/ticket-individual---parceiro";
    }

    @GetMapping("/leads/{id}/notas/novo")
    public String novaNota(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
        } catch (Exception e) {
            log.error("Erro ao carregar lead para nova nota: {}", e.getMessage());
            return "redirect:/parceiro/leads?error=Lead não encontrado";
        }
        model.addAttribute("leadId", id);
        return "parceiro/leads---parceiro/nova-nota---parceiro";
    }

    @GetMapping("/leads/{leadId}/notas/editar/{notaId}")
    public String editarNota(@PathVariable String leadId, @PathVariable String notaId, Model model) {
        try {
            co.ao.base.model.LeadDTO lead = leadService.buscarLead(leadId);
            model.addAttribute("lead", lead);
            model.addAttribute("leadId", leadId);
            model.addAttribute("notaId", notaId);
            
            if (lead != null && lead.getNotas() != null) {
                model.addAttribute("notaAtual", lead.getNotas().stream()
                        .filter(n -> n.getPublicId().equals(notaId))
                        .findFirst()
                        .orElse(null));
            }
        } catch (Exception e) {
            log.error("Erro ao carregar nota para edição: {}", e.getMessage());
            return "redirect:/parceiro/leads/" + leadId + "?error=Nota não encontrada";
        }
        return "parceiro/leads---parceiro/editar-nota---parceiro";
    }
}
