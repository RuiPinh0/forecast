package no.spond.forecast.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;        
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User {
    @Getter @Setter private String id;
    @Getter @Setter private String name;
    @Getter @Setter private String email;
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant updatedDate;
    @Getter @Setter private List<Team> teams; // List of teams the user is part of
}
