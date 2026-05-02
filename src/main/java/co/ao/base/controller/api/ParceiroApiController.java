package co.ao.base.controller.api;

import co.ao.base.service.api.ParceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/parceiros")
public class ParceiroApiController {

    @Autowired
    private ParceiroService parceiroService;

    @PostMapping
    public ResponseEntity<?> criarParceiro(
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "documento", required = false) MultipartFile documento,
            @RequestParam("dados") String dadosJson) {
        try {
            String publicId = parceiroService.criarParceiro(foto, documento, dadosJson);
            return ResponseEntity.ok(Map.of("publicId", publicId, "message", "Parceiro criado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarParceiro(
            @PathVariable String id,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "documento", required = false) MultipartFile documento,
            @RequestParam("dados") String dadosJson) {
        try {
            parceiroService.editarParceiro(id, foto, documento, dadosJson);
            return ResponseEntity.ok(Map.of("message", "Parceiro atualizado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarParceiro(@PathVariable String id) {
        try {
            parceiroService.eliminarParceiro(id);
            return ResponseEntity.ok(Map.of("message", "Parceiro eliminado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
