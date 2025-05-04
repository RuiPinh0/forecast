package no.spond.forecast.repository;

import org.springframework.stereotype.Repository;
import no.spond.forecast.model.Event;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    Event findByEventId(String eventId); // Assuming eventId is unique
    Event findByLocationId(String locationId); // Assuming locationId is unique
}
