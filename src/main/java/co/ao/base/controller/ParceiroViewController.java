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
                if (apiData.containsKey("totalFaturacao")) overview.put("faturacaoTotal", apiData.get("totalFaturacao"));
            }
        } catch (Exception e) {
            log.error("Erro ao obter overview do parceiro: {}", e.getMessage());
        }
        
        overview.put("ticketsAbertos", 0L);
        overview.put("ticketsAbertosPercent", 0.0);
        try {
            var ticketsPage = ticketService.listarTickets(0, 1000);
            if (ticketsPage != null && ticketsPage.getContent() != null) {
                long totalTickets = ticketsPage.getTotalElements();
                long abertos = ticketsPage.getContent().stream()
                        .filter(t -> "ABERTO".equalsIgnoreCase(t.getEstado()))
                        .count();
                
                double percent = 0.0;
                long totalBase = totalTickets; // 100% based on total tickets
                
                // Se o utilizador preferir basear a percentagem no total de leads:
                // long totalLeads = Long.parseLong(overview.get("totalLeads").toString());
                // totalBase = totalLeads;
                
                if (totalBase > 0) {
                    percent = ((double) abertos / totalBase) * 100;
                }
                
                overview.put("ticketsAbertos", abertos);
                overview.put("ticketsAbertosPercent", percent);
            }
        } catch (Exception e) {
            log.error("Erro ao obter tickets para overview: {}", e.getMessage());
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
    public String listarLeads(@RequestParam(required = false) Integer mes,
                              @RequestParam(required = false) Integer ano,
                              @RequestParam(required = false) String estado,
                              @RequestParam(defaultValue = "10") Integer tamanho,
                              Model model) {
        
        try {
            model.addAttribute("meses", dominioService.listarMeses());
        } catch (Exception e) {
            model.addAttribute("meses", java.util.Collections.emptyList());
        }

        model.addAttribute("mesSel", mes);
        model.addAttribute("anoSel", ano);
        model.addAttribute("estadoSel", estado);
        model.addAttribute("tamanhoSel", tamanho);

        String dataInicial = null;
        String dataFinal = null;

        if (mes != null && ano != null) {
            java.time.LocalDate start = java.time.LocalDate.of(ano, mes, 1);
            java.time.LocalDate end = start.plusMonths(1).minusDays(1);
            dataInicial = start.toString();
            dataFinal = end.toString();
        } else if (ano != null) {
            dataInicial = ano + "-01-01";
            dataFinal = ano + "-12-31";
        } else if (mes != null) {
            int currentYear = java.time.LocalDate.now().getYear();
            java.time.LocalDate start = java.time.LocalDate.of(currentYear, mes, 1);
            java.time.LocalDate end = start.plusMonths(1).minusDays(1);
            dataInicial = start.toString();
            dataFinal = end.toString();
        }

        try {
            boolean mostrarTodas = (estado == null || estado.isEmpty());

            model.addAttribute("leadsLead", (mostrarTodas || estado.equals("LEAD")) ? 
                leadService.listarLeads(0, tamanho, "LEAD", dataInicial, dataFinal) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsPendente", (mostrarTodas || estado.equals("PENDENTE")) ? 
                leadService.listarLeads(0, tamanho, "PENDENTE", dataInicial, dataFinal) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsConvertido", (mostrarTodas || estado.equals("CONVERTIDO")) ? 
                leadService.listarLeads(0, tamanho, "CONVERTIDO", dataInicial, dataFinal) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsPerdido", (mostrarTodas || estado.equals("PERDIDO")) ? 
                leadService.listarLeads(0, tamanho, "PERDIDO", dataInicial, dataFinal) : new co.ao.base.model.PageResponse<>());

        } catch (Exception e) {
            log.error("Erro ao listar leads do parceiro: {}", e.getMessage());
            model.addAttribute("leadsLead", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsPendente", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsConvertido", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsPerdido", new co.ao.base.model.PageResponse<>());
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
        } catch (Exception e) {
            log.warn("Erro ao carregar lead para edicao: {}. Usando fallback.", e.getMessage());
            co.ao.base.model.LeadDTO l = new co.ao.base.model.LeadDTO();
            l.setPublicId(id);
            model.addAttribute("lead", l);
        }
        try {
            model.addAttribute("pacotes", dominioService.listarPacotes());
        } catch (Exception e) {
            model.addAttribute("pacotes", java.util.Collections.emptyList());
        }
        return "parceiro/leads---parceiro/editar-lead---parceiro";
    }

    @GetMapping("/leads/{id}")
    public String verLead(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
        } catch (Exception e) {
            log.warn("Erro ao carregar lead individual: {}. Usando fallback.", e.getMessage());
            co.ao.base.model.LeadDTO l = new co.ao.base.model.LeadDTO();
            l.setPublicId(id);
            l.setNome("(Dados nao disponiveis)");
            model.addAttribute("lead", l);
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
        try {
            model.addAttribute("ticket", ticketService.buscarTicket(id));
        } catch (Exception e) {
            log.warn("Erro ao carregar ticket para edicao: {}. Usando fallback.", e.getMessage());
            co.ao.base.model.TicketDTO t = new co.ao.base.model.TicketDTO();
            t.setPublicId(id);
            model.addAttribute("ticket", t);
        }
        return "parceiro/tickets/editar-ticket---parceiro";
    }

    @GetMapping("/tickets/{id}")
    public String verTicket(@PathVariable String id,
                            @RequestParam(required = false) String conteudo,
                            @RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) String publicadoPorNome,
                            Model model) {
        try {
            model.addAttribute("ticket", ticketService.buscarTicket(id));
        } catch (Exception e) {
            log.warn("Erro ao carregar ticket individual: {}. Usando fallback.", e.getMessage());
            co.ao.base.model.TicketDTO t = new co.ao.base.model.TicketDTO();
            t.setPublicId(id);
            t.setConteudo(conteudo != null ? conteudo : "(Dados nao disponiveis)");
            t.setTipo(tipo != null ? tipo : "OUTRO");
            t.setEstado(estado != null ? estado : "ABERTO");
            t.setPublicadoPorNome(publicadoPorNome);
            model.addAttribute("ticket", t);
        }
        return "parceiro/tickets/ticket-individual---parceiro";
    }

    @GetMapping("/leads/{id}/notas/novo")
    public String novaNota(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
        } catch (Exception e) {
            log.error("Erro ao carregar lead para nova nota: {}", e.getMessage());
            return "redirect:/parceiro/leads?error=Lead desconhecido";
        }
        model.addAttribute("leadId", id);
        return "parceiro/leads---parceiro/criar-nota---parceiro";
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
