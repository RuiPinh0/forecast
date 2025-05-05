package no.spond.forecast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.spond.forecast.dto.ForecastDTO;
import no.spond.forecast.service.IWeatherService;

@RestController
@RequestMapping("/api/v1/forecast")
public class ForecastController { //here we could add implmentation with auth and then get values from auth token

    @Autowired
    IWeatherService weatherService;
    
    @GetMapping("/")
    @ResponseBody
    public ForecastDTO getForecast(@RequestParam Long eventId) throws Exception {
        return weatherService.getForecast(eventId);
    }
    
}