package no.spond.forecast.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import no.spond.forecast.dto.ForecastDTO;
import no.spond.forecast.model.Event;
import no.spond.forecast.model.Forecast;
import no.spond.forecast.model.Location;
import no.spond.forecast.repository.EventRepository;
import no.spond.forecast.repository.ForecastRepository;

@RestClientTest(WeatherService.class)
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private RestClient metClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private ForecastRepository forecastRepository;

    @Test
    void testGetForecast_EventNotFound() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act
        ForecastDTO result = weatherService.getForecast(eventId);

        // Assert
        assertNull(result);
        verify(eventRepository, times(1)).findById(eventId);
        verifyNoInteractions(forecastRepository);
    }

    @Test
    void testGetForecast_WithExistingForecasts() {
        // Arrange
        Location location = new Location();
        location.setLatitude("60.0");
        location.setLongitude("10.0");
        location.setName("Oslo");
        location.setStreet("Karl Johans gate 1");
        location.setCreatedDate(Instant.now());
        location.setUpdatedDate(Instant.now());

        Long eventId = 1L;
        Event event = new Event();
        event.setEventStartDate(Instant.now().plusSeconds(3600)); // 1 hour in the future
        event.setEventEndDate(Instant.now().plusSeconds(7200)); // 2 hours in the future
        event.setLocation(location);

        Forecast existingForecast = new Forecast();
        existingForecast.setTime(Instant.now().plusSeconds(3600)); // 1 hour in the future
        existingForecast.setAirTemperature(20.0);
        existingForecast.setWindSpeed(5.0);

        List<Forecast> existingForecasts = List.of(existingForecast);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(forecastRepository.findByEventId(eventId)).thenReturn(existingForecasts);

        // Act
        ForecastDTO result = weatherService.getForecast(eventId);

        // Assert
        assertNotNull(result);
        assertEquals(20.0, result.getAirTemperature());
        assertEquals(5.0, result.getWindSpeed());
        verify(eventRepository, times(1)).findById(eventId);
        verify(forecastRepository, times(1)).findByEventId(eventId);
    
    }
}