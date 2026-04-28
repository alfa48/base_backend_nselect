package co.ao.base.service.api;

import co.ao.base.model.PageResponse;
import co.ao.base.model.UserDTO;
import co.ao.base.model.UsuarioCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;

@Slf4j
@Service
public class UsuarioService extends BaseApiService {

    /**
     * Lista os utilizadores do cliente autenticado.
     * Usa o publicId da sessão para construir a rota correta.
     */
    public PageResponse<UserDTO> getMeusUsuarios(int page, int size) {
        String publicId = getSessionPublicId();
        String endpoint;

        if (publicId != null) {
            // Rota com publicId do cliente: /usuarios/meus?clienteId={publicId}
            endpoint = UriComponentsBuilder.fromPath("/usuarios/meus")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .toUriString();
            log.debug("A listar utilizadores para cliente publicId={}", publicId);
        } else {
            log.warn("PublicId não disponível na sessão. A usar rota genérica.");
            endpoint = UriComponentsBuilder.fromPath("/usuarios/meus")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .toUriString();
        }

        return get(endpoint, new ParameterizedTypeReference<PageResponse<UserDTO>>() {});
    }

    /**
     * Cria um novo utilizador.
     */
    public String criarUsuario(UsuarioCreateRequest request) {
        String endpoint = "/usuarios";
        return post(endpoint, request, String.class);
    }

    /**
     * Busca um utilizador pelo seu publicId ou ID numérico.
     */
    public UserDTO getUsuarioByPublicId(String id) {
        String endpoint = "/usuarios/" + id;
        log.debug("Chamando API para buscar utilizador: {}", endpoint);
        return get(endpoint, UserDTO.class);
    }

    /**
     * Atualiza os dados de um utilizador.
     */
    public String atualizarUsuario(String id, UsuarioCreateRequest request) {
        String endpoint = "/usuarios/" + id;
        log.debug("Chamando API para atualizar utilizador: {}", endpoint);
        return put(endpoint, request, String.class);
    }

    /**
     * Elimina um utilizador.
     */
    public void eliminarUsuario(String id) {
        String endpoint = "/usuarios/" + id;
        log.debug("Chamando API para eliminar utilizador: {}", endpoint);
        delete(endpoint);
    }

    /**
     * Atualiza a foto de um utilizador.
     */
    public void atualizarFoto(String publicId, org.springframework.web.multipart.MultipartFile foto) {
        try {
            String endpoint = "/usuarios/foto/" + publicId;
            org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            
            // Converter MultipartFile para Resource
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ByteArrayResource(foto.getBytes()) {
                @Override
                public String getFilename() {
                    return foto.getOriginalFilename();
                }
            };
            
            body.add("foto", resource);
            postMultipart(endpoint, body, String.class);
            log.info("Foto atualizada com sucesso para {}", publicId);
        } catch (java.io.IOException e) {
            log.error("Erro ao ler bytes da foto: {}", e.getMessage());
            throw new RuntimeException("Falha ao processar arquivo de imagem", e);
        }
    }
}
