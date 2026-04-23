package co.ao.base.service.api;

import co.ao.base.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Service
public abstract class BaseApiService {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected HttpSession session;

    /**
     * Método genérico para execução de chamadas POST.
     * Centraliza LOGS, Headers e Tratamento de Erros.
     */
    protected <T> T post(String endpoint, Object body, Class<T> responseType) {
        String url = Constant.BASE_URL + endpoint;
        try {
            log.info("API REQUEST: POST {} | Payload: {}", url, body);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, getRequestEntity(body), responseType);
            log.info("API RESPONSE: {} | Status: {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                log.warn("Token expirado ({}). A tentar refresh...", e.getStatusCode().value());
                if (tryRefreshToken()) {
                    // Retry com novo token
                    ResponseEntity<T> retry = restTemplate.exchange(url, HttpMethod.POST, getRequestEntity(body), responseType);
                    log.info("API RETRY RESPONSE: {} | Status: {}", url, retry.getStatusCode());
                    return retry.getBody();
                }
            }
            log.error("API HTTP ERROR: {} | Status: {} | Body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            log.error("API CONNECTION ERROR: {} | Message: {}", url, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API UNEXPECTED ERROR: {} | Message: {}", url, e.getMessage());
            throw new RestClientException("Erro inesperado na chamada da API: " + e.getMessage(), e);
        }
    }

    /**
     * Método para chamadas MULTIPART (Upload de arquivos).
     */
    protected <T> T postMultipart(String endpoint, org.springframework.util.MultiValueMap<String, Object> body, Class<T> responseType) {
        String url = Constant.BASE_URL + endpoint;
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<org.springframework.util.MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("API REQUEST (MULTIPART): POST {}", url);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            log.info("API RESPONSE: {} | Status: {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("API MULTIPART ERROR: {} | Status: {} | Body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("API MULTIPART UNEXPECTED ERROR: {} | Message: {}", url, e.getMessage());
            throw new RestClientException("Erro no upload: " + e.getMessage(), e);
        }
    }

    /**
     * Método genérico para execução de chamadas GET.
     */
    protected <T> T get(String endpoint, Class<T> responseType) {
        String url = Constant.BASE_URL + endpoint;
        try {
            log.info("API REQUEST: GET {}", url);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, getRequestEntity(null), responseType);
            log.info("API RESPONSE: {} | Status: {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("API HTTP ERROR: {} | Status: {} | Body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            log.error("API CONNECTION ERROR: {} | Message: {}", url, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API UNEXPECTED ERROR: {} | Message: {}", url, e.getMessage());
            throw new RestClientException("Erro inesperado na chamada da API: " + e.getMessage(), e);
        }
    }

    /**
     * Método genérico para execução de chamadas GET com suporte a Generics (ex: List, Page).
     */
    protected <T> T get(String endpoint, ParameterizedTypeReference<T> responseType) {
        String url = Constant.BASE_URL + endpoint;
        try {
            log.info("API REQUEST: GET (Generic) {}", url);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, getRequestEntity(null), responseType);
            log.info("API RESPONSE: {} | Status: {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                log.warn("Token expirado ({}). A tentar refresh...", e.getStatusCode().value());
                if (tryRefreshToken()) {
                    ResponseEntity<T> retry = restTemplate.exchange(url, HttpMethod.GET, getRequestEntity(null), responseType);
                    log.info("API RETRY RESPONSE: {} | Status: {}", url, retry.getStatusCode());
                    return retry.getBody();
                }
            }
            log.error("API HTTP ERROR: {} | Status: {} | Body: {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            log.error("API CONNECTION ERROR: {} | Message: {}", url, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API UNEXPECTED ERROR: {} | Message: {}", url, e.getMessage());
            throw new RestClientException("Erro inesperado na chamada da API: " + e.getMessage(), e);
        }
    }

    /**
     * Tenta fazer refresh do token usando o refresh token armazenado na sessão.
     * @return true se o refresh foi bem-sucedido, false caso contrário.
     */
    protected boolean tryRefreshToken() {
        String refreshToken = (String) session.getAttribute("refreshToken");
        if (refreshToken == null) {
            log.warn("Sem refresh token disponível na sessão.");
            return false;
        }
        try {
            String refreshUrl = Constant.BASE_URL + "/auth/refresh-token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", Constant.API_KEY);
            headers.set("Authorization", "Bearer " + refreshToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(refreshUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object newToken = response.getBody().get("access_token");
                Object newRefresh = response.getBody().get("refresh_token");
                if (newToken != null) {
                    session.setAttribute("token", newToken.toString());
                    if (newRefresh != null) session.setAttribute("refreshToken", newRefresh.toString());
                    log.info("Token renovado com sucesso.");
                    return true;
                }
            }
        } catch (Exception ex) {
            log.error("Falha ao renovar token: {}", ex.getMessage());
        }
        return false;
    }

    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String token = (String) session.getAttribute("token");
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        
        headers.set("X-API-KEY", Constant.API_KEY);
        return headers;
    }

    protected <T> HttpEntity<T> getRequestEntity(T body) {
        return new HttpEntity<>(body, getHeaders());
    }

    /**
     * Retorna o utilizador autenticado da sessão.
     */
    protected co.ao.base.model.UserDTO getSessionUser() {
        return (co.ao.base.model.UserDTO) session.getAttribute("user");
    }

    /**
     * Retorna o publicId do utilizador autenticado (UUID).
     * Útil para construir rotas que precisam do ID do cliente.
     */
    protected String getSessionPublicId() {
        co.ao.base.model.UserDTO user = getSessionUser();
        return user != null ? user.getPublicId() : null;
    }

    /**
     * Retorna o id numérico do utilizador autenticado.
     */
    protected Long getSessionUserId() {
        co.ao.base.model.UserDTO user = getSessionUser();
        return user != null ? user.getId() : null;
    }
}
