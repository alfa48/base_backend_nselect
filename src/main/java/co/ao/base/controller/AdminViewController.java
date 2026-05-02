package co.ao.base.controller;

import co.ao.base.service.api.AdminService;
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

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired
    private ParceiroService parceiroService;

    @Autowired
    private LeadService leadService;

    @Autowired
    private MaterialApoioService materialApoioService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard Admin");
        
        // Overview Stats
        try {
            model.addAttribute("overview", adminService.getOverview());
        } catch (Exception e) {
            model.addAttribute("overview", Map.of(
                "totalParceiros", 0, "totalParceirosTrend", 0.0,
                "totalLeads", 0, "totalLeadsTrend", 0.0,
                "ticketsAbertos", 0, "ticketsAbertosTrend", 0.0
            ));
        }

        // Recent Partners
        try {
            var partners = parceiroService.listarParceiros(0, 5, null, null, null);
            model.addAttribute("recentParceiros", partners != null ? partners.getContent() : new java.util.ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("recentParceiros", new java.util.ArrayList<>());
        }

        // Recent Tickets
        try {
            var tickets = ticketService.listarTickets(0, 5);
            model.addAttribute("recentTickets", tickets != null ? tickets.getContent() : new java.util.ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("recentTickets", new java.util.ArrayList<>());
        }

        return "admin/dashboard---admin";
    }

    @GetMapping("/parceiros")
    public String listarParceiros(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String provincia,
                                 @RequestParam(required = false) String tipo,
                                 Model model) {
        model.addAttribute("parceiros", parceiroService.listarParceiros(pagina, tamanho, nome, provincia, tipo));
        return "admin/parceiros---admin/parceiros-main---admin";
    }

    @GetMapping("/parceiros/novo")
    public String novoParceiro(Model model) {
        return "admin/parceiros---admin/criar-parceiro---admin";
    }

    @GetMapping("/parceiros/editar/{id}")
    public String editarParceiro(@PathVariable String id, Model model) {
        model.addAttribute("parceiro", parceiroService.buscarParceiro(id));
        return "admin/parceiros---admin/editar-parceiro---admin";
    }

    @GetMapping("/parceiros/ver/{id}")
    public String verParceiro(@PathVariable String id, Model model) {
        model.addAttribute("parceiro", parceiroService.buscarParceiro(id));
        return "admin/parceiros---admin/ver-parceiro---admin";
    }

    @GetMapping("/leads")
    public String listarLeads(@RequestParam(defaultValue = "0") int pagina,
                              @RequestParam(defaultValue = "10") int tamanho,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String dataInicial,
                              @RequestParam(required = false) String dataFinal,
                              @RequestParam(required = false) String parceiro,
                              Model model) {
        model.addAttribute("leads", leadService.listarTodosLeadsAdmin(pagina, tamanho, estado, dataInicial, dataFinal, parceiro));
        return "admin/leads---admin/leads-main---admin";
    }

    @GetMapping("/leads/{id}")
    public String verLead(@PathVariable String id, Model model) {
        model.addAttribute("lead", leadService.buscarLead(id));
        return "admin/leads---admin/lead-individual---admin";
    }

    @GetMapping("/materiais")
    public String listarMateriais(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 Model model) {
        model.addAttribute("materiais", materialApoioService.listarMateriaisAdmin(pagina, tamanho, nome, tipo));
        return "admin/material-de-apoio---admin/material-de-apoio---admin";
    }

    @GetMapping("/materiais/novo")
    public String novoMaterial(Model model) {
        return "admin/material-de-apoio---admin/criar-material---admin";
    }

    @GetMapping("/materiais/editar/{id}")
    public String editarMaterial(@PathVariable String id, Model model) {
        model.addAttribute("material", materialApoioService.buscarMaterial(id));
        return "admin/material-de-apoio---admin/editar-material---admin";
    }

    @GetMapping("/materiais/{id}")
    public String verMaterial(@PathVariable String id, Model model) {
        return "admin/material-de-apoio---admin/material-individual---admin";
    }

    @GetMapping("/tickets")
    public String listarTickets(@RequestParam(defaultValue = "0") int pagina,
                               @RequestParam(defaultValue = "10") int tamanho,
                               Model model) {
        try {
            model.addAttribute("tickets", ticketService.listarTickets(pagina, tamanho));
        } catch (Exception e) {
            model.addAttribute("tickets", null);
            model.addAttribute("error", "Não foi possível carregar os tickets no momento.");
        }
        return "admin/tickets---admin/tickets-main---admin";
    }

    @GetMapping("/tickets/{id}")
    public String verTicket(@PathVariable String id, Model model) {
        model.addAttribute("ticket", ticketService.buscarTicket(id));
        return "admin/tickets---admin/ticket-individual---admin";
    }
}
