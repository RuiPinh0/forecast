package no.spond.forecast.dto;

import java.time.Instant;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class TeamDTO {
     @Getter @Setter private String id;
     @Getter @Setter private String name;
     @Getter @Setter private UserDTO createdBy;
     @Getter @Setter private Instant createdDate;
     @Getter @Setter private String updatedBy;
     @Getter @Setter private Instant updatedDate;
     @Getter @Setter private String teamType; // e.g., "football", "handball", "basketball". Here could be a enum
     @Getter @Setter private List<UserDTO> members; // List of users in the team
     @Getter @Setter private List<EventDTO> events; // List of events associated with the team
}
