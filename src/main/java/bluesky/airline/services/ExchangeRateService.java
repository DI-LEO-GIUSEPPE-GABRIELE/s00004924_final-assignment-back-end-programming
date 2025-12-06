package bluesky.airline.services;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    private final RestTemplate http = new RestTemplate();
    private final String apiKey;
    private final String baseUrl;

    public ExchangeRateService(@Value("${exchangerate.apiKey}") String apiKey,
            @Value("${exchangerate.baseUrl:https://v6.exchangerate-api.com/v6}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public BigDecimal convert(BigDecimal amount, String base, String target) {
        String url = baseUrl + "/" + apiKey + "/latest/" + base;
        Map<?, ?> res = http.getForObject(url, Map.class);
        if (res == null)
            return amount;
        Object rates = res.get("conversion_rates");
        if (rates instanceof Map<?, ?> m) {
            Object rate = m.get(target);
            if (rate instanceof Number n) {
                return amount.multiply(BigDecimal.valueOf(n.doubleValue()));
            }
        }
        return amount;
    }
}
