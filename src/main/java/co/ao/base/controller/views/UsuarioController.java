package co.ao.base.controller.views;

import co.ao.base.model.PageResponse;
import co.ao.base.model.UserDTO;
import co.ao.base.model.UsuarioCreateRequest;
import co.ao.base.service.api.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/utilizadores")
    public String todosUtilizadores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        try {
            // A API é 0-based, o UI é 1-based
            int apiPage = (page > 0) ? page - 1 : 0;
            PageResponse<UserDTO> response = usuarioService.getMeusUsuarios(apiPage, size);
            
            model.addAttribute("usuarios", response.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", response.getTotalPages());
            model.addAttribute("size", size);
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar utilizadores: " + e.getMessage());
        }

        return "fiinika/utilizadores/todos-utilizadores";
    }

    @GetMapping("/utilizadores/novo")
    public String criarUtilizador(Model model) {
        model.addAttribute("usuarioForm", new UsuarioCreateRequest());
        return "fiinika/utilizadores/criar-utilizador";
    }

    @PostMapping("/utilizadores/novo")
    public String salvarUtilizador(@ModelAttribute("usuarioForm") UsuarioCreateRequest usuario,
                                   @RequestParam(value = "foto", required = false) org.springframework.web.multipart.MultipartFile foto,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs,
                                   Model model) {
        try {
            String rawResponse = usuarioService.criarUsuario(usuario);
            String publicId = null;

            if (rawResponse != null) {
                // Tenta ver se é um JSON
                if (rawResponse.trim().startsWith("{")) {
                    try {
                        org.springframework.boot.json.JsonParser parser = org.springframework.boot.json.JsonParserFactory.getJsonParser();
                        java.util.Map<String, Object> map = parser.parseMap(rawResponse);
                        if (map.containsKey("publicId")) publicId = map.get("publicId").toString();
                        else if (map.containsKey("public_id")) publicId = map.get("public_id").toString();
                    } catch (Exception e) {
                        // Se falhar o parse, talvez seja apenas uma string com aspas ou algo assim
                        publicId = rawResponse.replaceAll("\"", "").trim();
                    }
                } else {
                    // Assume que a resposta é o próprio ID ou uma mensagem simples
                    publicId = rawResponse.replaceAll("\"", "").trim();
                }
            }
            
            // Se houver foto, fazemos o upload usando o publicId retornado
            if (foto != null && !foto.isEmpty() && publicId != null && publicId.length() > 10) { // Check simples para garantir que temos um UUID
                usuarioService.atualizarFoto(publicId, foto);
            }
            redirectAttrs.addFlashAttribute("successMessage", "Utilizador criado com sucesso!");
            return "redirect:/utilizadores";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao criar utilizador: " + e.getMessage());
            model.addAttribute("usuarioForm", usuario);
            return "fiinika/utilizadores/criar-utilizador";
        }
    }
}
