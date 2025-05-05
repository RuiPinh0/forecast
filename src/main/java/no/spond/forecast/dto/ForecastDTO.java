
package no.spond.forecast.dto;

import java.time.Instant;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import no.spond.forecast.model.Forecast;

@NoArgsConstructor
@ToString
public class ForecastDTO {
    public ForecastDTO(Forecast forecast) {
        this.time = forecast.getTime();
        this.windSpeed = forecast.getWindSpeed();
        this.airTemperature = forecast.getAirTemperature();    }
    @Getter @Setter private Instant time;
    @Getter @Setter private double windSpeed; 
    @Getter @Setter private double airTemperature;
    @Getter @Setter private String forecastValue; 

}

