package co.ao.base.service.api;

import co.ao.base.model.PageResponse;
import co.ao.base.model.ParceiroDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class ParceiroService extends BaseApiService {

    public PageResponse<ParceiroDTO> listarParceiros(int pagina, int tamanho, String nome, String provinciaPublicId, String tipoParceiroPublicId) {
        StringBuilder url = new StringBuilder("/admin/parceiros?pagina=").append(pagina).append("&tamanho=").append(tamanho);
        if (nome != null && !nome.isEmpty()) url.append("&nome=").append(nome);
        if (provinciaPublicId != null && !provinciaPublicId.isEmpty()) url.append("&provinciaPublicId=").append(provinciaPublicId);
        if (tipoParceiroPublicId != null && !tipoParceiroPublicId.isEmpty()) url.append("&tipoParceiroPublicId=").append(tipoParceiroPublicId);
        
        return get(url.toString(), new ParameterizedTypeReference<PageResponse<ParceiroDTO>>() {});
    }

    public ParceiroDTO buscarParceiro(String publicId) {
        return get("/admin/parceiros/" + publicId, ParceiroDTO.class);
    }

    public String criarParceiro(MultipartFile foto, MultipartFile documento, String dadosJson) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Adicionando a parte JSON com o Content-Type correto
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonEntity = new HttpEntity<>(dadosJson, jsonHeaders);
        body.add("dados", jsonEntity);

        if (foto != null && !foto.isEmpty()) {
            body.add("foto", foto.getResource());
        }
        if (documento != null && !documento.isEmpty()) {
            body.add("documento", documento.getResource());
        }

        return postMultipart("/admin/parceiros", body, String.class);
    }

    public void editarParceiro(String publicId, MultipartFile foto, MultipartFile documento, String dadosJson) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonEntity = new HttpEntity<>(dadosJson, jsonHeaders);
        body.add("dados", jsonEntity);

        if (foto != null && !foto.isEmpty()) {
            body.add("foto", foto.getResource());
        }
        if (documento != null && !documento.isEmpty()) {
            body.add("documento", documento.getResource());
        }

        putMultipart("/admin/parceiros/" + publicId, body, Void.class);
    }

    public void eliminarParceiro(String publicId) {
        delete("/admin/parceiros/" + publicId);
    }

    public org.springframework.http.ResponseEntity<byte[]> downloadDocumento(String publicId) {
        return getEntity("/admin/parceiros/" + publicId + "/documento", byte[].class);
    }

    public org.springframework.http.ResponseEntity<byte[]> downloadFotoUsuario(String usuarioPublicId) {
        return getEntity("/usuarios/" + usuarioPublicId + "/foto", byte[].class);
    }

    public org.springframework.http.ResponseEntity<byte[]> exibirArquivo(String caminho) {
        return getEntity("/arquivos/exibir?caminho=" + caminho, byte[].class);
    }

    /**
     * Obtém os indicadores de performance específicos do parceiro autenticado.
     * Inclui total de leads, leads convertidos, faturação e tendências (%) de crescimento.
     */
    public Map<String, Object> getOverview() {
        return get("/leads/overview", Map.class);
    }
}
