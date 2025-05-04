package no.spond.forecast.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString

@Entity
@Table(name = "team")
public class Team {
     @Getter @Setter private String id;
     @Getter @Setter private String name;
     @Getter @Setter private User createdBy;
     @Getter @Setter private Instant createdDate;
     @Getter @Setter private String updatedBy;
     @Getter @Setter private Instant updatedDate;
     @Getter @Setter private String teamType; // e.g., "football", "handball", "basketball". Here could be a enum
     @Getter @Setter private List<User> members; // List of users in the team
     @Getter @Setter private List<Event> events; // List of events associated with the team
}
