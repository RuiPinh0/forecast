package no.spond.forecast.service;

import no.spond.forecast.dto.ForecastDTO;

public interface IWeatherService {
    public ForecastDTO getForecast(Long eventId);
}
