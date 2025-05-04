package no.spond.forecast.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;        
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    @Getter @Setter private String id;
    @Getter @Setter private String name;
    @Getter @Setter private String email;
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant updatedDate;
    @Getter @Setter private List<TeamDTO> teams; // List of teams the user is part of
}
