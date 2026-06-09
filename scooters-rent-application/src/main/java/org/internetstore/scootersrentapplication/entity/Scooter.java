package org.internetstore.scootersrentapplication.entity;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;

@Setter
@Getter
@Entity
@Table(name = "scooters")
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "charge_level")
    private Integer chargeLevel;

    @Enumerated(EnumType.STRING)
    private ScooterStatus status;

    // Wea use GPS coordinates
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

}