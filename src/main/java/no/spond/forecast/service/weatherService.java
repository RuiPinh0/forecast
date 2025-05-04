package no.spond.forecast.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.spond.forecast.dto.ForecastDTO;
import no.spond.forecast.model.Event;
import no.spond.forecast.model.Forecast;
import no.spond.forecast.repository.EventRepository;

@Service
public class weatherService implements IWeatherService {
    
    @Autowired
    private EventRepository eventRepository;

    private RestClient metClient;
    weatherService() {
        metClient = RestClient.builder()
        .requestFactory(new HttpComponentsClientHttpRequestFactory())
        .baseUrl("https://api.met.no/weatherapi/locationforecast/2.0")
        .defaultHeader("User-Agent", "ForecastService/0.1 ruip.90@gmail.com")
        .build();
    }

    
    public ForecastDTO getForecast(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        Instant ifModifiedSinceInstant = ZonedDateTime.now().minusMonths(1).toInstant(); //set default value to 1 month ago
        if (event != null && event.getLocation() != null) {
            if(event.getEventStartDate() != null){
                Duration timeToEventDays = Duration.between(Instant.now(), event.getEventStartDate());
                
                // If the event is more than 7 days in the future we do nothing yet and throw exception
                if (Duration.ofDays(7).compareTo(timeToEventDays) < 0) {
                    throw new IllegalArgumentException("Event start date is more than 7 days in the future");
                }
                
                if (!event.getForecasts().isEmpty() && getForecastForCurrentTime(event.getForecasts()) != null) {
                    Duration timeToEvent = Duration.between(Instant.now(), getForecastForCurrentTime(event.getForecasts()).getTime());
                    if (getForecastForCurrentTime(event.getForecasts()).getLastUpdatedDate() != null && Duration.ofHours(2).compareTo(timeToEvent) < 0) { // If the forecast was last updated less than 2 hours ago, we return the cached forecast if exists, otherwise we fetch new data                                                   
                        return new ForecastDTO(getForecastForCurrentTime(event.getForecasts())); 
                    }
                }

                if (!event.getForecasts().isEmpty() && getForecastForCurrentTime(event.getForecasts()).getLastUpdatedDate() != null) {
                    ifModifiedSinceInstant = getForecastForCurrentTime(event.getForecasts()).getLastUpdatedDate();
                }

                String ifModifiedSinceString = DateTimeFormatter
                .RFC_1123_DATE_TIME
                .withZone(ZoneId.of("GMT"))
                .format(ifModifiedSinceInstant); //format the date to RFC 1123 format to set header

                String response = getCurrentWeather(event.getLocation().getLatitude(), event.getLocation().getLongitude(), ifModifiedSinceString);//here we do the request to the met api
                if (response != null) {
                    List<ForecastDTO> forecastList = parseWeatherData(response);
                    List<ForecastDTO> filteredForecasts = sliceList(event, forecastList); //slice the list to get only the forecasts that are in the event period
                    List<Forecast> newForecasts = mapToForecastList(filteredForecasts);

                    if (event.getForecasts() != null && !event.getForecasts().isEmpty()) {
                        List<Forecast> existingForecasts = event.getForecasts();
                
                        for (Forecast newForecast : newForecasts) {
                            boolean updated = false;
                
                            for (Forecast existingForecast : existingForecasts) {
                                if (existingForecast.getTime().equals(newForecast.getTime())) {
                                    existingForecast.setWindSpeed(newForecast.getWindSpeed());
                                    existingForecast.setAirTemperature(newForecast.getAirTemperature());
                                    existingForecast.setLastUpdatedDate(Instant.now());
                                    updated = true;
                                    break;
                                }
                            }
                
                            if (!updated) {
                                existingForecasts.add(newForecast);
                            }
                        }
                    } else {
                        event.setForecasts(newForecasts);
                    }
                
                    eventRepository.save(event);
                }
            }
            return new ForecastDTO(getForecastForCurrentTime(event.getForecasts()));
        }
        else 
            throw new IllegalArgumentException("Event not found or event location is null");
    }
    
    private String getCurrentWeather(String latitude, String longitude, String time) {
        return metClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/compact")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .build())
            .header("If-Modified-Since", time)
            .retrieve()
            .body(String.class);
    }
    
    private List<ForecastDTO> parseWeatherData(String response) {
        List<ForecastDTO> forecastList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode timeseries = rootNode.path("properties").path("timeseries");

            for (JsonNode timeEntry : timeseries) {
                ForecastDTO forecast = new ForecastDTO();

                String time = timeEntry.path("time").asText();
                forecast.setTime(Instant.parse(time));
                forecast.setWindSpeed(timeEntry.path("data").path("instant").path("details").path("wind_speed").asDouble());
                forecast.setAirTemperature(timeEntry.path("data").path("instant").path("details").path("air_temperature").asDouble());
                forecastList.add(forecast);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return forecastList;
    }

    private List<ForecastDTO> sliceList(Event event, List<ForecastDTO> forecastList) {
                
        // Define the start and end time for the period
        Instant eventStartDateHour = event.getEventStartDate().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        Instant eventEndDateHour = event.getEventEndDate().truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        // Filter the forecasts within the period
        List<ForecastDTO> matchingForecasts = forecastList.stream()
            .filter(forecast -> {
                Instant forecastTime = forecast.getTime().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
                return !forecastTime.isBefore(eventStartDateHour) && forecastTime.isBefore(eventEndDateHour);
            })
            .toList();

        if (matchingForecasts.isEmpty()) {
            throw new IllegalArgumentException("No matching forecasts found for the specified period.");
        }

        return matchingForecasts;
    }

    private List<Forecast> mapToForecastList(List<ForecastDTO> forecastDTOList) {
        return forecastDTOList.stream()
            .map(dto -> new Forecast(dto.getTime(), dto.getAirTemperature(), dto.getWindSpeed(), Instant.now(), Instant.now()))
            .toList();
    }

    private Forecast getForecastForCurrentTime(List<Forecast> forecastList) {
        Instant now = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
    
        // Find the forecast to the current hour
        Forecast currentForecast = forecastList.stream()
            .filter(forecast -> forecast.getTime().truncatedTo(java.time.temporal.ChronoUnit.HOURS).equals(now))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No forecasts available for the current time."));
    
        return currentForecast;
    }
}