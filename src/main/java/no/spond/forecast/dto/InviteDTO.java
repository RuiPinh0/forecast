package no.spond.forecast.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class InviteDTO {
    @Getter @Setter private String id;
    @Getter @Setter private String eventId;
    @Getter @Setter private UserDTO user;
    @Getter @Setter private String status; // e.g., "accepted", "declined", "pending". Here could be a enum
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant updatedDate;
}
