package no.spond.forecast.model;

import java.time.Instant;
import java.util.List;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Entity
@Table(name = "event")
public class Event {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;
    @Getter @Setter private String name;
    @Getter @Setter private Instant createdDate;
    @Getter @Setter private Instant eventStartDate;
    @Getter @Setter private Instant eventEndDate;
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @Getter @Setter private Location location;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter private List<Forecast> forecasts;
    @Getter @Setter private String updatedBy;
    @Getter @Setter private Instant updatedDate;
    @Getter @Setter private String status; // e.g., "scheduled", "completed", "canceled". Here could be a enum
}