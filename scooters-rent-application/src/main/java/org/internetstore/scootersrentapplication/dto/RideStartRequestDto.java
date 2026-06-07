package org.internetstore.scootersrentapplication.dto;

public class RideStartRequestDto {
    private Integer userId;
    private Integer scooterId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScooterId() {
        return scooterId;
    }

    public void setScooterId(Integer scooterId) {
        this.scooterId = scooterId;
    }
}
