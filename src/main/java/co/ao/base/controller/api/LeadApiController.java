package co.ao.base.controller.api;

import co.ao.base.model.LeadCreateRequest;
import co.ao.base.service.api.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadApiController {

    @Autowired
    private LeadService leadService;

    @PostMapping
    public ResponseEntity<?> criarLead(@RequestBody LeadCreateRequest request) {
        try {
            String publicId = leadService.criarLead(request);
            return ResponseEntity.ok(Map.of("message", "Lead criado com sucesso", "publicId", publicId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> editarLead(@PathVariable String publicId,
                                       @RequestPart(value = "comprovativo", required = false) MultipartFile comprovativo,
                                       @RequestPart("dados") String dadosJson) {
        try {
            leadService.editarLead(publicId, comprovativo, dadosJson);
            return ResponseEntity.ok(Map.of("message", "Lead atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> eliminarLead(@PathVariable String publicId) {
        try {
            leadService.eliminarLead(publicId);
            return ResponseEntity.ok(Map.of("message", "Lead eliminado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{publicId}/comprovativo")
    public ResponseEntity<?> uploadComprovativo(@PathVariable String publicId,
                                               @RequestParam("file") MultipartFile file) {
        try {
            leadService.uploadComprovativo(publicId, file);
            return ResponseEntity.ok(Map.of("message", "Comprovativo carregado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Notas de Lead
    @PostMapping("/{publicId}/notas")
    public ResponseEntity<?> adicionarNota(@PathVariable String publicId, @RequestBody Map<String, String> body) {
        try {
            String nota = body.get("nota");
            String notaId = leadService.adicionarNota(publicId, nota);
            return ResponseEntity.ok(Map.of("message", "Nota adicionada com sucesso", "publicId", notaId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{leadId}/notas/{notaId}")
    public ResponseEntity<?> editarNota(@PathVariable String leadId, @PathVariable String notaId, @RequestBody Map<String, String> body) {
        try {
            String nota = body.get("nota");
            leadService.editarNota(leadId, notaId, nota);
            return ResponseEntity.ok(Map.of("message", "Nota atualizada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{leadId}/notas/{notaId}")
    public ResponseEntity<?> eliminarNota(@PathVariable String leadId, @PathVariable String notaId) {
        try {
            leadService.eliminarNota(leadId, notaId);
            return ResponseEntity.ok(Map.of("message", "Nota eliminada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
