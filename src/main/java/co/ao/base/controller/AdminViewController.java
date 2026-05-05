package co.ao.base.controller;

import co.ao.base.service.api.AdminService;
import co.ao.base.service.api.LeadService;
import co.ao.base.util.Constant;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    private static final Logger log = LoggerFactory.getLogger(AdminViewController.class);

    @Autowired
    private co.ao.base.service.api.DominioService dominioService;

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
        try {
            model.addAttribute("provincias", dominioService.listarProvincias());
            model.addAttribute("tipos", dominioService.listarTiposParceiro());
            model.addAttribute("parceiros", parceiroService.listarParceiros(pagina, tamanho, nome, provincia, tipo));
        } catch (Exception e) {
            log.error("Erro ao listar parceiros: {}", e.getMessage());
            model.addAttribute("parceiros", new co.ao.base.model.PageResponse<>());
            model.addAttribute("provincias", java.util.Collections.emptyList());
            model.addAttribute("tipos", java.util.Collections.emptyList());
        }
        return "admin/parceiros---admin/parceiros-main---admin";
    }

    @GetMapping("/parceiros/novo")
    public String novoParceiro(Model model) {
        try {
            model.addAttribute("provincias", dominioService.listarProvincias());
            model.addAttribute("tiposParceiro", dominioService.listarTiposParceiro());
        } catch (Exception e) {
            model.addAttribute("provincias", java.util.Collections.emptyList());
            model.addAttribute("tiposParceiro", java.util.Collections.emptyList());
        }
        return "admin/parceiros---admin/criar-parceiro---admin";
    }

    @GetMapping("/parceiros/editar/{id}")
    public String editarParceiro(@PathVariable String id, Model model) {
        try {
            co.ao.base.model.ParceiroDTO parceiro = parceiroService.buscarParceiro(id);
            processarFicheirosParceiro(parceiro);
            
            model.addAttribute("parceiro", parceiro);
            model.addAttribute("provincias", dominioService.listarProvincias());
            model.addAttribute("tiposParceiro", dominioService.listarTiposParceiro());
        } catch (Exception e) {
            log.error("Erro ao carregar parceiro para edição: {}", e.getMessage());
            return "redirect:/admin/parceiros?error=Erro ao carregar dados";
        }
        return "admin/parceiros---admin/editar-parceiro---admin";
    }

    @GetMapping("/parceiros/ver/{id}")
    public String verParceiro(@PathVariable String id, Model model) {
        try {
            co.ao.base.model.ParceiroDTO parceiro = parceiroService.buscarParceiro(id);
            processarFicheirosParceiro(parceiro);

            log.info("DEBUG PARCEIRO FINAL: ID={}, Foto={}, Doc={}", id, parceiro.getFotoUrl(), parceiro.getDocumentoUrl());
            model.addAttribute("parceiro", parceiro);
        } catch (Exception e) {
            log.error("Erro ao ver parceiro: {}", e.getMessage());
            return "redirect:/admin/parceiros?error=Parceiro não encontrado";
        }
        return "admin/parceiros---admin/ver-parceiro---admin";
    }

    /**
     * Centraliza a lógica de transformação de URLs de ficheiros para usar proxies seguros.
     */
    private void processarFicheirosParceiro(co.ao.base.model.ParceiroDTO parceiro) {
        // Proxy para a Foto (Usando formato de caminho para ser mais amigável ao browser)
        String fotoUrl = parceiro.getFotoUrl();
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            String caminho = fotoUrl;
            if (caminho.contains("/files/")) {
                caminho = caminho.substring(caminho.indexOf("/files/") + 7);
            } else if (caminho.startsWith("http")) {
                try {
                    java.net.URL url = new java.net.URL(caminho);
                    caminho = url.getPath();
                    if (caminho.startsWith("/")) caminho = caminho.substring(1);
                    if (caminho.startsWith("api/v1/")) caminho = caminho.substring(7);
                } catch (Exception e) {
                    log.warn("Falha ao processar caminho: {}", caminho);
                }
            }
            // Novo formato: /admin/parceiros/foto/caminho/completo.png
            parceiro.setFotoUrl("/admin/parceiros/foto/" + caminho);
        } else {
            parceiro.setFotoUrl("/images/avatar-placeholder.png");
        }
        
        // Proxy para o Documento
        if (parceiro.isTemAnexo()) {
            parceiro.setDocumentoUrl("/admin/parceiros/download-documento/" + parceiro.getPublicId());
        }
    }

    @GetMapping("/admin/parceiros/foto/**")
    public org.springframework.http.ResponseEntity<byte[]> exibirFotoParceiro(jakarta.servlet.http.HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String caminho = fullPath.replaceFirst("/admin/parceiros/foto/", "");
        
        log.info(">>> PROXY FOTO HIT: Recebido pedido para caminho '{}'", caminho);
        try {
            log.info("Proxy de Foto: Solicitando à API externa: '{}'", caminho);
            org.springframework.http.ResponseEntity<byte[]> response = parceiroService.exibirArquivo(caminho);
            
            log.info("Proxy de Foto: Resposta da API: Status={}, Content-Type={}", 
                     response.getStatusCode(), response.getHeaders().getContentType());
            
            return response;
        } catch (Exception e) {
            log.error("Erro CRÍTICO no Proxy de Foto para '{}': {}", caminho, e.getMessage());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parceiros/download-documento/{id}")
    public org.springframework.http.ResponseEntity<byte[]> downloadDocumentoParceiro(@PathVariable String id) {
        try {
            return parceiroService.downloadDocumento(id);
        } catch (Exception e) {
            log.error("Erro ao descarregar documento do parceiro: {}", e.getMessage());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/leads")
    public String listarLeads(@RequestParam(required = false) Integer mes,
                              @RequestParam(required = false) Integer ano,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String parceiro,
                              @RequestParam(defaultValue = "10") Integer tamanho,
                              Model model) {
        
        log.info("LISTAR LEADS ADMIN - Filtros recebidos: mes={}, ano={}, estado={}, parceiro={}, tamanho={}", mes, ano, estado, parceiro, tamanho);
        
        // Dados para os seletores (Carregados sempre)
        try {
            model.addAttribute("parceiros", parceiroService.listarParceiros(0, 100, null, null, null).getContent());
            model.addAttribute("meses", dominioService.listarMeses());
        } catch (Exception e) {
            log.error("Erro ao carregar dados de domínio para filtros: {}", e.getMessage());
            model.addAttribute("parceiros", java.util.Collections.emptyList());
            model.addAttribute("meses", java.util.Collections.emptyList());
        }

        // Passar filtros selecionados de volta para manter o estado na UI
        model.addAttribute("mesSel", mes);
        model.addAttribute("anoSel", ano);
        model.addAttribute("estadoSel", estado);
        model.addAttribute("parceiroSel", parceiro);

        String dataInicial = null;
        String dataFinal = null;

        // Lógica de Datas Flexível
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
            // Se estado for vazio ou nulo, carregamos todas as colunas.
            // Caso contrário, carregamos apenas a coluna correspondente ao estado selecionado.
            boolean mostrarTodas = (estado == null || estado.isEmpty());

            model.addAttribute("leadsLead", (mostrarTodas || estado.equals("LEAD")) ? 
                leadService.listarTodosLeadsAdmin(0, tamanho, "LEAD", dataInicial, dataFinal, parceiro) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsPendente", (mostrarTodas || estado.equals("PENDENTE")) ? 
                leadService.listarTodosLeadsAdmin(0, tamanho, "PENDENTE", dataInicial, dataFinal, parceiro) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsConvertido", (mostrarTodas || estado.equals("CONVERTIDO")) ? 
                leadService.listarTodosLeadsAdmin(0, tamanho, "CONVERTIDO", dataInicial, dataFinal, parceiro) : new co.ao.base.model.PageResponse<>());
            
            model.addAttribute("leadsPerdido", (mostrarTodas || estado.equals("PERDIDO")) ? 
                leadService.listarTodosLeadsAdmin(0, tamanho, "PERDIDO", dataInicial, dataFinal, parceiro) : new co.ao.base.model.PageResponse<>());

        } catch (Exception e) {
            log.error("Erro ao listar leads no Admin: {}", e.getMessage());
            model.addAttribute("leadsLead", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsPendente", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsConvertido", new co.ao.base.model.PageResponse<>());
            model.addAttribute("leadsPerdido", new co.ao.base.model.PageResponse<>());
        }

        // Passar tamanho atual de volta para a UI
        model.addAttribute("tamanhoSel", tamanho);

        return "admin/leads---admin/leads-main---admin";
    }

    @GetMapping("/leads/{id}")
    public String verLead(@PathVariable String id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarLead(id));
        } catch (Exception e) {
            log.error("Erro ao ver lead: {}", e.getMessage());
            return "redirect:/admin/leads?error=Lead não encontrado";
        }
        return "admin/leads---admin/lead-individual---admin";
    }

    @GetMapping("/materiais")
    public String listarMateriais(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 Model model) {
        try {
            model.addAttribute("materiais", materialApoioService.listarMateriaisAdmin(pagina, tamanho, nome, tipo));
        } catch (Exception e) {
            model.addAttribute("materiais", new co.ao.base.model.PageResponse<>());
            model.addAttribute("error", "Não foi possível carregar os materiais no momento.");
        }
        return "admin/material-de-apoio---admin/material-de-apoio---admin";
    }


    @GetMapping("/materiais/novo")
    public String novoMaterial(Model model) {
        try {
            model.addAttribute("tiposParceiro", dominioService.listarTiposParceiro());
        } catch (Exception e) {
            model.addAttribute("tiposParceiro", java.util.Collections.emptyList());
        }
        return "admin/material-de-apoio---admin/criar-material---admin";
    }

    @GetMapping("/materiais/editar/{id}")
    public String editarMaterial(@PathVariable String id, Model model) {
        try {
            model.addAttribute("material", materialApoioService.buscarMaterial(id));
            model.addAttribute("tiposParceiro", dominioService.listarTiposParceiro());
        } catch (Exception e) {
            log.error("Erro ao carregar material para edição: {}", e.getMessage());
            return "redirect:/admin/materiais?error=Erro ao carregar dados";
        }
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
            model.addAttribute("tickets", new co.ao.base.model.PageResponse<>());
            model.addAttribute("error", "Não foi possível carregar os tickets no momento.");
        }
        return "admin/tickets---admin/tickets-main---admin";
    }


    @GetMapping("/tickets/{id}")
    public String verTicket(@PathVariable String id, Model model) {
        try {
            model.addAttribute("ticket", ticketService.buscarTicket(id));
        } catch (Exception e) {
            log.error("Erro ao ver ticket: {}", e.getMessage());
            return "redirect:/admin/tickets?error=Ticket não encontrado";
        }
        return "admin/tickets---admin/ticket-individual---admin";
    }
}
