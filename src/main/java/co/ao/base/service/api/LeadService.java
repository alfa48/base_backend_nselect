package co.ao.base.service.api;

import co.ao.base.model.LeadCreateRequest;
import co.ao.base.model.LeadDTO;
import co.ao.base.model.PageResponse;
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
public class LeadService extends BaseApiService {

    public PageResponse<LeadDTO> listarLeads(int pagina, int tamanho, String estado, String dataInicial, String dataFinal) {
        StringBuilder url = new StringBuilder("/leads?pagina=").append(pagina).append("&tamanho=").append(tamanho);
        if (estado != null && !estado.isEmpty()) url.append("&estado=").append(estado);
        if (dataInicial != null && !dataInicial.isEmpty()) url.append("&dataInicial=").append(dataInicial);
        if (dataFinal != null && !dataFinal.isEmpty()) url.append("&dataFinal=").append(dataFinal);
        
        return get(url.toString(), new ParameterizedTypeReference<PageResponse<LeadDTO>>() {});
    }

    public PageResponse<LeadDTO> listarTodosLeadsAdmin(int pagina, int tamanho, String estado, String dataInicial, String dataFinal, String parceiroPublicId) {
        StringBuilder url = new StringBuilder("/leads/admin/todos?pagina=").append(pagina).append("&tamanho=").append(tamanho);
        if (estado != null && !estado.isEmpty()) url.append("&estado=").append(estado);
        if (dataInicial != null && !dataInicial.isEmpty()) url.append("&dataInicial=").append(dataInicial);
        if (dataFinal != null && !dataFinal.isEmpty()) url.append("&dataFinal=").append(dataFinal);
        if (parceiroPublicId != null && !parceiroPublicId.isEmpty()) url.append("&parceiroPublicId=").append(parceiroPublicId);
        
        return get(url.toString(), new ParameterizedTypeReference<PageResponse<LeadDTO>>() {});
    }

    public LeadDTO buscarLead(String publicId) {
        return get("/leads/" + publicId, LeadDTO.class);
    }

    public String criarLead(LeadCreateRequest dados) {
        return post("/leads", dados, String.class);
    }

    public void editarLead(String publicId, MultipartFile comprovativo, String dadosJson) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonEntity = new HttpEntity<>(dadosJson, jsonHeaders);
        body.add("dados", jsonEntity);

        if (comprovativo != null && !comprovativo.isEmpty()) {
            body.add("comprovativo", comprovativo.getResource());
        }

        putMultipart("/leads/" + publicId, body, Void.class);
    }

    public void eliminarLead(String publicId) {
        delete("/leads/" + publicId);
    }

    public void uploadComprovativo(String publicId, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        postMultipart("/leads/" + publicId + "/comprovativo", body, String.class);
    }

    public Map<String, Object> getOverview() {
        return get("/leads/overview", Map.class);
    }

    // Notas
    public String adicionarNota(String leadPublicId, String nota) {
        Map<String, String> body = Map.of("nota", nota);
        return post("/leads/" + leadPublicId + "/notas", body, String.class);
    }

    public void editarNota(String leadPublicId, String notaPublicId, String nota) {
        Map<String, String> body = Map.of("nota", nota);
        put("/leads/" + leadPublicId + "/notas/" + notaPublicId, body, Void.class);
    }

    public void eliminarNota(String leadPublicId, String notaPublicId) {
        delete("/leads/" + leadPublicId + "/notas/" + notaPublicId);
    }
}
