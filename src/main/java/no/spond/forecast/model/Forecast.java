
package no.spond.forecast.model;

import java.time.Instant;

import jakarta.persistence.*;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Entity
@Table(name = "forecast")
public class Forecast {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @Getter @Setter private Event event;
    @Getter @Setter private Instant time;
    @Getter @Setter private double airTemperature;
    @Getter @Setter private double windSpeed;
    @Getter @Setter private String forecastValue; // e.g., "sunny", "rainy", "cloudy"
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant lastUpdatedDate;
   
    public Forecast(Event event, Instant time, double airTemperature, double windSpeed, Instant createdDate, Instant lastUpdatedDate) {
        this.event = event;
        this.time = time;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
    }

}