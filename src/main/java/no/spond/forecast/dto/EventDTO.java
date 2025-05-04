package no.spond.forecast.dto;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EventDTO {
    
    @Getter @Setter private String eventId;
    @Getter @Setter private String eventName;
    @Getter @Setter private string host; //not sure if this should be a user or a team
    @Getter @Setter private LocationDTO location;
    @Getter @Setter private ForecastDTO forecast;




    @Getter @Setter private UserDTO createdBy;
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant eventStartDate;
    @Getter @Setter private Instant eventEndDate;
    @Getter @Setter private LocationDTO location;
    @Getter @Setter private ForecastDTO forecast;
    @Getter @Setter private String updatedBy;
    @Getter @Setter private Instant updatedDate;
    @Getter @Setter private List<InviteDTO> invitationList;
    @Getter @Setter private String status; // e.g., "scheduled", "completed", "canceled". Here could be a enum
}
