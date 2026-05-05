package co.ao.base.service.api;

import co.ao.base.model.MaterialApoioDTO;
import co.ao.base.model.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MaterialApoioService extends BaseApiService {

    public PageResponse<MaterialApoioDTO> listarMateriais(int pagina, int tamanho, String nome, String tipoConteudo) {
        StringBuilder url = new StringBuilder("/materiais-apoio?pagina=").append(pagina).append("&tamanho=").append(tamanho);
        if (nome != null && !nome.isEmpty()) url.append("&nome=").append(nome);
        if (tipoConteudo != null && !tipoConteudo.isEmpty()) url.append("&tipoConteudo=").append(tipoConteudo);
        
        return get(url.toString(), new ParameterizedTypeReference<PageResponse<MaterialApoioDTO>>() {});
    }

    public PageResponse<MaterialApoioDTO> listarMateriaisAdmin(int pagina, int tamanho, String nome, String tipoConteudo) {
        log.info(">>> SERVICE HIT: listarMateriaisAdmin (Forçando uso de /materiais-apoio)");
        // O Admin usa o mesmo endpoint de listagem que o parceiro, mas com permissões de Admin
        StringBuilder url = new StringBuilder("/materiais-apoio?pagina=").append(pagina).append("&tamanho=").append(tamanho);
        if (nome != null && !nome.isEmpty()) url.append("&nome=").append(nome);
        if (tipoConteudo != null && !tipoConteudo.isEmpty()) url.append("&tipoConteudo=").append(tipoConteudo);

        return get(url.toString(), new ParameterizedTypeReference<PageResponse<MaterialApoioDTO>>() {});
    }

    public String criarMaterial(MultipartFile arquivo, String dadosJson) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonEntity = new HttpEntity<>(dadosJson, jsonHeaders);
        body.add("dados", jsonEntity);

        if (arquivo != null && !arquivo.isEmpty()) {
            body.add("arquivo", arquivo.getResource());
        }

        return postMultipart("/admin/materiais-apoio", body, String.class);
    }

    public void editarMaterial(String publicId, MultipartFile arquivo, String dadosJson) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonEntity = new HttpEntity<>(dadosJson, jsonHeaders);
        body.add("dados", jsonEntity);

        if (arquivo != null && !arquivo.isEmpty()) {
            body.add("arquivo", arquivo.getResource());
        }

        putMultipart("/admin/materiais-apoio/" + publicId, body, Void.class);
    }

    public void eliminarMaterial(String publicId) {
        delete("/admin/materiais-apoio/" + publicId);
    }

    public MaterialApoioDTO buscarMaterial(String publicId) {
        return get("/admin/materiais-apoio/" + publicId, MaterialApoioDTO.class);
    }
}
