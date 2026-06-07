package org.internetstore.scootersrentapplication.dto;

public class RideEndRequestDto {
    private Integer rideId;
    private Double distance;

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
