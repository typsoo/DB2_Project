package org.internetstore.scootersrentapplication.entity;

import jakarta.persistence.*;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;

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

    private Double latitude;
    private Double longitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ScooterStatus getStatus() {
        return status;
    }

    public void setStatus(ScooterStatus status) {
        this.status = status;
    }

    public Integer getChargeLevel() {
        return chargeLevel;
    }

    public void setChargeLevel(Integer chargeLevel) {
        this.chargeLevel = chargeLevel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}