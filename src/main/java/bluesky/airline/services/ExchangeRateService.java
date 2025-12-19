package bluesky.airline.services;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Service for currency exchange rates
@Service
public class ExchangeRateService {
    private final RestTemplate http = new RestTemplate();
    @Value("${exchangerate.apiKey}")
    private String apiKey;
    @Value("${exchangerate.baseUrl:https://v6.exchangerate-api.com/v6}")
    private String baseUrl;

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
