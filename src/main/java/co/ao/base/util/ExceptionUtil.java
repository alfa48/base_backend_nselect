package co.ao.base.util;

import org.springframework.web.client.HttpStatusCodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class ExceptionUtil {
    
    /**
     * Extrai a mensagem de erro formatada.
     * Se for um erro da API externa (HttpStatusCodeException), tenta extrair as chaves "msg", "error" ou "message" do corpo JSON.
     * Caso contrário, retorna e.getMessage() original.
     */
    public static String getMessage(Exception e) {
        if (e instanceof HttpStatusCodeException) {
            try {
                String responseBody = ((HttpStatusCodeException) e).getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(responseBody);
                    if (root.has("msg")) {
                        return root.get("msg").asText();
                    } else if (root.has("error")) {
                        return root.get("error").asText();
                    } else if (root.has("message")) {
                        return root.get("message").asText();
                    }
                    return responseBody;
                }
            } catch (Exception ex) {
                // Fallback para a mensagem original se falhar o parsing do JSON
                return e.getMessage();
            }
        }
        return e.getMessage();
    }
}
