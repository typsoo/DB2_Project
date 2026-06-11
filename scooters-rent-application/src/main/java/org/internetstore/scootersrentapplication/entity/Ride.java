package org.internetstore.scootersrentapplication.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // connection with users table
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // connection with scooters table
    @ManyToOne
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private Double distance;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

}