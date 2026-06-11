package org.internetstore.scootersrentapplication.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "telemetry_logs")
public class TelemetryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    private Double latitude;
    private Double longitude;

}
