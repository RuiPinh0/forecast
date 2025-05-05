package no.spond.forecast.repository;

import org.springframework.stereotype.Repository;
import no.spond.forecast.model.Forecast;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface ForecastRepository extends CrudRepository<Forecast, Long> {
    List<Forecast> findByEventId(long eventId);
}
