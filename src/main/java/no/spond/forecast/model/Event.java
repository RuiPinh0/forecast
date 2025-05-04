package no.spond.forecast.model;

import java.time.Instant;
import java.util.List;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Entity
@Table(name = "event")
public class Event {
    
    @Getter @Setter private String id;
    @Getter @Setter private String name;
    @Getter @Setter private User host; //not sure if this should be a user or a team
    @Getter @Setter private User createdBy;
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant eventStartDate;
    @Getter @Setter private Instant eventEndDate;
    @Getter @Setter private Location location;
    @Getter @Setter private List<Forecast> forecasts;
    @Getter @Setter private String updatedBy;
    @Getter @Setter private Instant updatedDate;
    @Getter @Setter private List<Invite> invitationList;
    @Getter @Setter private String status; // e.g., "scheduled", "completed", "canceled". Here could be a enum
}
