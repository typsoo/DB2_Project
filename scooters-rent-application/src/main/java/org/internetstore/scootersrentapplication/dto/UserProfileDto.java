package org.internetstore.scootersrentapplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class UserProfileDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private LocalDateTime createdAt;

}