package no.spond.forecast.repository;

import org.springframework.stereotype.Repository;
import no.spond.forecast.model.Forecast;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface ForecastRepository extends CrudRepository<Forecast, Long> {
    Forecast findByEventId(String eventId); // Assuming eventId is unique
    Forecast findByLocationId(String locationId); // Assuming locationId is unique
}
