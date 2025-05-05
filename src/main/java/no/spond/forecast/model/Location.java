package no.spond.forecast.model;

import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "location")
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;
    @Getter @Setter private String name;
    @Getter @Setter private String street;
    @Getter @Setter private String latitude; // Should be added a validation for this string to be a valid latitude
    @Getter @Setter private String longitude; // Should be added a validation for this string to be a valid longitude
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant updatedDate;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter private List<Event> events = new ArrayList<>();
}
