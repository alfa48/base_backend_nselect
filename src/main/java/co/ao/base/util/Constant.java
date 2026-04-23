package co.ao.base.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {

    public static String API_KEY;
    public static String BASE_URL;

    @Value("${api.key}")
    public void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    @Value("${api.base-url}")
    public void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
