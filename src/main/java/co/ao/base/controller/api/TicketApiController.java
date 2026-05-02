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
            ticketService.editarTicket(publicId, request);
            return ResponseEntity.ok(Map.of("message", "Ticket atualizado com sucesso"));
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
