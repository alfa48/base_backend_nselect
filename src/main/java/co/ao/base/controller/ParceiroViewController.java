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

@Controller
@RequestMapping("/parceiro")
public class ParceiroViewController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private MaterialApoioService materialApoioService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ParceiroService parceiroService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("overview", parceiroService.getOverview());
        model.addAttribute("recentLeads", leadService.listarLeads(0, 5, null, null, null));
        return "parceiro/dashboard-parceiros---parceiro";
    }

    @GetMapping("/leads")
    public String listarLeads(@RequestParam(defaultValue = "0") int pagina,
                              @RequestParam(defaultValue = "10") int tamanho,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String dataInicial,
                              @RequestParam(required = false) String dataFinal,
                              Model model) {
        model.addAttribute("leads", leadService.listarLeads(pagina, tamanho, estado, dataInicial, dataFinal));
        return "parceiro/leads---parceiro/leads-main---parceiro";
    }

    @GetMapping("/leads/novo")
    public String novoLead(Model model) {
        return "parceiro/leads---parceiro/novo-lead---parceiro";
    }

    @GetMapping("/leads/editar/{id}")
    public String editarLead(@PathVariable String id, Model model) {
        model.addAttribute("lead", leadService.buscarLead(id));
        return "parceiro/leads---parceiro/editar-lead---parceiro";
    }

    @GetMapping("/leads/{id}")
    public String verLead(@PathVariable String id, Model model) {
        model.addAttribute("lead", leadService.buscarLead(id));
        return "parceiro/leads---parceiro/lead-individual---parceiro";
    }

    @GetMapping("/materiais")
    public String listarMateriais(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 Model model) {
        model.addAttribute("materiais", materialApoioService.listarMateriais(pagina, tamanho, nome, tipo));
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
        model.addAttribute("tickets", ticketService.listarTickets(pagina, tamanho));
        return "parceiro/tickets/ticket---parceiro";
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
        model.addAttribute("ticket", ticketService.buscarTicket(id));
        return "parceiro/tickets/ticket-individual--parceiro";
    }

    @GetMapping("/leads/{id}/notas/novo")
    public String novaNota(@PathVariable String id, Model model) {
        model.addAttribute("leadId", id);
        model.addAttribute("lead", leadService.buscarLead(id));
        return "parceiro/leads---parceiro/criar-nota---parceiro";
    }

    @GetMapping("/leads/{leadId}/notas/editar/{notaId}")
    public String editarNota(@PathVariable String leadId, @PathVariable String notaId, Model model) {
        co.ao.base.model.LeadDTO lead = leadService.buscarLead(leadId);
        co.ao.base.model.LeadDTO.LeadNotaDTO notaAtual = null;
        if (lead != null && lead.getNotas() != null) {
            for (co.ao.base.model.LeadDTO.LeadNotaDTO n : lead.getNotas()) {
                if (n.getPublicId().equals(notaId)) {
                    notaAtual = n;
                    break;
                }
            }
        }
        model.addAttribute("lead", lead);
        model.addAttribute("leadId", leadId);
        model.addAttribute("notaId", notaId);
        model.addAttribute("notaAtual", notaAtual);
        return "parceiro/leads---parceiro/editar-nota---parceiro";
    }
}
