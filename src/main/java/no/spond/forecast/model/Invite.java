package no.spond.forecast.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "invite")
public class Invite {
    @Getter @Setter private String id;
    @Getter @Setter private String eventId;
    @Getter @Setter private User user;
    @Getter @Setter private String status; // e.g., "accepted", "declined", "pending". Here could be a enum
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant updatedDate;
}
