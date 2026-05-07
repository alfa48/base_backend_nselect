package co.ao.base.controller.api;

import co.ao.base.service.api.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketApiController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> criarTicket(@RequestBody Object request) {
        try {
            String publicId = ticketService.criarTicket(request);
            return ResponseEntity.ok(Map.of("message", "Ticket aberto com sucesso", "publicId", publicId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> editarTicket(@PathVariable String publicId, @RequestBody Object request) {
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
            if (isAdmin) {
                ticketService.editarTicketAdmin(publicId, request);
            } else {
                ticketService.editarTicket(publicId, request);
            }
            return ResponseEntity.ok(Map.of("message", "Ticket atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{publicId}/estado")
    public ResponseEntity<?> alterarEstadoTicket(@PathVariable String publicId, @RequestBody Object request) {
        try {
            ticketService.alterarEstadoTicketAdmin(publicId, request);
            return ResponseEntity.ok(Map.of("message", "Estado atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> eliminarTicket(@PathVariable String publicId) {
        try {
            ticketService.eliminarTicket(publicId);
            return ResponseEntity.ok(Map.of("message", "Ticket eliminado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
