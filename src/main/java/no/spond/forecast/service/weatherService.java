package no.spond.forecast.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.spond.forecast.dto.ForecastDTO;
import no.spond.forecast.model.Event;
import no.spond.forecast.model.Forecast;
import no.spond.forecast.repository.EventRepository;
import no.spond.forecast.repository.ForecastRepository;

@Service
public class WeatherService implements IWeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final String USERAGENT_HEADER = "User-Agent";


    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ForecastRepository forecastRepository;

    @Value("${weather.api.base-url}") 
    private String baseUrl;
    @Value("${weather.api.header.user-agent}") 
    private String userAgent;

    private RestClient metClient = RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(USERAGENT_HEADER, userAgent)
        .build();
    
    @Override
    public ForecastDTO getForecast(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Instant ifModifiedSinceInstant = ZonedDateTime.now().minusMonths(1).toInstant(); //set default value to 1 month ago
        if (eventOptional.isPresent() ) {
            Event event = eventOptional.get();
            if(event.getLocation() != null && event.getEventStartDate() != null){
                Duration timeToEventDays = Duration.between(Instant.now(), event.getEventStartDate());
                
                if (Duration.ofDays(7).compareTo(timeToEventDays) < 0) {
                    logger.info("Event is more than 7 days in the future. No forecast available yet!");
                    return null;
                }
                List<Forecast> existingForecasts = forecastRepository.findByEventId(eventId);
                if (!existingForecasts.isEmpty() && getForecastForCurrentTime(existingForecasts) != null) {
                    Duration timeToEvent = Duration.between(Instant.now(), getForecastForCurrentTime(existingForecasts).getTime());
                    if (getForecastForCurrentTime(existingForecasts).getLastUpdatedDate() != null && Duration.ofHours(2).compareTo(timeToEvent) < 0) { // If the forecast was last updated less than 2 hours ago, we return the cached forecast if exists, otherwise we fetch new data                                                   
                        return new ForecastDTO(getForecastForCurrentTime(existingForecasts)); 
                    }
                }

                if (!existingForecasts.isEmpty() && getForecastForCurrentTime(existingForecasts).getLastUpdatedDate() != null) {
                    ifModifiedSinceInstant = getForecastForCurrentTime(existingForecasts).getLastUpdatedDate();
                }

                String ifModifiedSinceString = DateTimeFormatter
                .RFC_1123_DATE_TIME
                .withZone(ZoneId.of("GMT"))
                .format(ifModifiedSinceInstant); //format the date to RFC 1123 format to set header

                String response = getCurrentWeather(event.getLocation().getLatitude(), event.getLocation().getLongitude(), ifModifiedSinceString);//here we do the request to the met api
                if (response != null) {
                    List<ForecastDTO> forecastList = parseWeatherData(response);
                    List<ForecastDTO> filteredForecasts = sliceList(event, forecastList); 
                    
                    if (filteredForecasts.isEmpty()) {
                        filteredForecasts = new ArrayList<>();
                        filteredForecasts.add(getForecastForEventTime(event, forecastList)); 
                        logger.info("No matching forecasts found for the specified period. Using the closest forecast to the event time.");
                    }

                    List<Forecast> newForecasts = mapToForecastList(filteredForecasts, event);

                    if (existingForecasts != null && !existingForecasts.isEmpty()) {
                                        
                        for (Forecast newForecast : newForecasts) {
                            boolean updated = false;
                
                            for (Forecast existingForecast : existingForecasts) {
                                if (existingForecast.getTime().equals(newForecast.getTime())) {
                                    existingForecast.setWindSpeed(newForecast.getWindSpeed());
                                    existingForecast.setAirTemperature(newForecast.getAirTemperature());
                                    existingForecast.setLastUpdatedDate(Instant.now());
                                    forecastRepository.save(existingForecast);
                                    updated = true;
                                    break;
                                }
                            }
                            
                            return new ForecastDTO(getForecastForCurrentTime(existingForecasts)); //return the forecast for the current time
                        
                        }
                    } else {
                        newForecasts.forEach(forecast -> forecast.setEvent(event)); 
                    
                        newForecasts.forEach(forecast -> forecastRepository.save(forecast));
                        return new ForecastDTO(getForecastForCurrentTime(newForecasts));
                    }
                }
                logger.error("No response from weather API or response is empty");
            }
            logger.error("Event location or starting date is still null");
        }
        else 
            logger.error("Event not found");
            return null; // Return null if the event is not found or location is null
    }
    
    private String getCurrentWeather(String latitude, String longitude, String time) {
        return metClient.get()
            .uri(uriBuilder -> uriBuilder
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
                JsonNode nextXHours = getClosestNextXHours(timeEntry.path("data"));
            if (nextXHours != null) {
                forecast.setForecastValue(nextXHours.path("summary").path("symbol_code").asText());
            }
                forecastList.add(forecast);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return forecastList;
    }

    private JsonNode getClosestNextXHours(JsonNode dataNode) {
        if (dataNode.has("next_1_hours")) {
            return dataNode.path("next_1_hours");
        } else if (dataNode.has("next_6_hours")) {
            return dataNode.path("next_6_hours");
        } else if (dataNode.has("next_12_hours")) {
            return dataNode.path("next_12_hours");
        }
        return null;
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
            logger.info("No matching forecasts found for the specified period.");
        }

        return matchingForecasts;
    }

    private ForecastDTO getForecastForEventTime(Event event, List<ForecastDTO> forecastList) {
        Instant eventTime = event.getEventStartDate().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
    
        // Find the forecast closest to the event's time
        return forecastList.stream()
            .min((forecast1, forecast2) -> {
                Duration diff1 = Duration.between(eventTime, forecast1.getTime()).abs();
                Duration diff2 = Duration.between(eventTime, forecast2.getTime()).abs();
                return diff1.compareTo(diff2);
            })
            .orElseThrow(() -> new IllegalArgumentException("No forecasts available for the event time."));
    }

    private List<Forecast> mapToForecastList(List<ForecastDTO> forecastDTOList, Event event) {
        return forecastDTOList.stream()
            .map(dto -> new Forecast(event, dto.getTime(), dto.getAirTemperature(), dto.getWindSpeed(), Instant.now(), Instant.now()))
            .toList();
    }

    private Forecast getForecastForCurrentTime(List<Forecast> forecastList) {
        Instant now = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
    
        // Find the forecast to the closest hour
        return forecastList.stream()
            .min((forecast1, forecast2) -> {
                Duration diff1 = Duration.between(now, forecast1.getTime()).abs();
                Duration diff2 = Duration.between(now, forecast2.getTime()).abs();
                return diff1.compareTo(diff2);
            })
            .orElseThrow(() -> new IllegalArgumentException("No forecasts available for the current time."));
    
    }
}