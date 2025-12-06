package study_project.demo.services;

import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import study_project.demo.entities.Airport;
import study_project.demo.entities.Flight;
import study_project.demo.entities.WeatherData;
import study_project.demo.repositories.WeatherDataRepository;

@Service
public class WeatherService {
    private final RestTemplate http = new RestTemplate();
    private final WeatherDataRepository weatherRepo;
    private final String apiKey;
    private final String baseUrl;

    public WeatherService(WeatherDataRepository weatherRepo,
                          @Value("${openweather.apiKey}") String apiKey,
                          @Value("${openweather.baseUrl:https://api.openweathermap.org/data/2.5/weather}") String baseUrl) {
        this.weatherRepo = weatherRepo;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public WeatherData refreshForFlight(Flight flight, Airport departureAirport) {
        String q = departureAirport.getCity();
        String url = baseUrl + "?q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8) + "&units=metric&appid=" + apiKey;
        Map<?,?> res = http.getForObject(url, Map.class);
        Double temp = null;
        String desc = null;
        if (res != null) {
            Object main = res.get("main");
            if (main instanceof Map<?,?> m && m.get("temp") instanceof Number n) temp = n.doubleValue();
            Object weather = res.get("weather");
            if (weather instanceof java.util.List<?> list && !list.isEmpty()) {
                Object w0 = list.get(0);
                if (w0 instanceof Map<?,?> wm && wm.get("description") instanceof String s) desc = s;
            }
        }
        WeatherData wd = weatherRepo.findByFlight(flight).orElseGet(WeatherData::new);
        wd.setFlight(flight);
        wd.setTemperature(temp);
        wd.setDescription(desc);
        wd.setRetrievedAt(Instant.now());
        return weatherRepo.save(wd);
    }
}
