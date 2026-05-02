package co.ao.base.controller.api;

import co.ao.base.service.api.MaterialApoioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/materiais-apoio")
public class MaterialApoioApiController {

    @Autowired
    private MaterialApoioService materialApoioService;

    @PostMapping
    public ResponseEntity<?> criarMaterial(@RequestPart("arquivo") MultipartFile arquivo,
                                          @RequestPart("dados") String dadosJson) {
        try {
            String publicId = materialApoioService.criarMaterial(arquivo, dadosJson);
            return ResponseEntity.ok(Map.of("message", "Material criado com sucesso", "publicId", publicId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> editarMaterial(@PathVariable String publicId,
                                           @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
                                           @RequestPart("dados") String dadosJson) {
        try {
            materialApoioService.editarMaterial(publicId, arquivo, dadosJson);
            return ResponseEntity.ok(Map.of("message", "Material atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> eliminarMaterial(@PathVariable String publicId) {
        try {
            materialApoioService.eliminarMaterial(publicId);
            return ResponseEntity.ok(Map.of("message", "Material eliminado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
