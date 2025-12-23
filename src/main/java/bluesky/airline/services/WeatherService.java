package bluesky.airline.services;

import org.springframework.beans.factory.annotation.Autowired;
import bluesky.airline.entities.Flight;
import java.time.Instant;
import org.springframework.web.client.RestTemplate;
import bluesky.airline.repositories.WeatherDataRepository;
import bluesky.airline.entities.WeatherData;
import java.util.List;
import java.util.Map;
import bluesky.airline.dto.weather.WeatherRespDTO;
import bluesky.airline.entities.Airport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Service for WeatherData entities
@Service
public class WeatherService {
    private final RestTemplate http = new RestTemplate();
    @Autowired
    private WeatherDataRepository weatherRepo;
    @Value("${openweather.apiKey}")
    private String apiKey;
    @Value("${openweather.baseUrl:https://api.openweathermap.org/data/2.5/weather}")
    private String baseUrl;

    // Refresh weather data for a flight
    public WeatherData refreshForFlight(Flight flight, Airport departureAirport) {
        String q = departureAirport.getCity();
        String url = baseUrl + "?q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8)
                + "&units=metric&appid=" + apiKey;
        Map<?, ?> res = http.getForObject(url, Map.class);
        Double temp = null;
        String desc = null;
        if (res != null) {
            Object main = res.get("main");
            if (main instanceof Map<?, ?> m && m.get("temp") instanceof Number n)
                temp = n.doubleValue();
            Object weather = res.get("weather");
            if (weather instanceof List<?> list && !list.isEmpty()) {
                Object w0 = list.get(0);
                if (w0 instanceof Map<?, ?> wm && wm.get("description") instanceof String s)
                    desc = s;
            }
        }
        WeatherData wd = weatherRepo.findByFlight(flight).orElseGet(WeatherData::new);
        wd.setFlight(flight);
        wd.setTemperature(temp);
        wd.setDescription(desc);
        wd.setRetrievedAt(Instant.now());
        return weatherRepo.save(wd);
    }

    // Convert a WeatherData entity to a WeatherRespDTO
    public WeatherRespDTO toDTO(WeatherData w) {
        WeatherRespDTO dto = new WeatherRespDTO();
        dto.setId(w.getId());
        dto.setFlightId(w.getFlight().getId());
        dto.setTemperature(w.getTemperature());
        dto.setDescription(w.getDescription());
        dto.setRetrievedAt(w.getRetrievedAt());
        return dto;
    }
}
