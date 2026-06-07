package org.internetstore.scootersrentapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScootersRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScootersRentApplication.class, args);
    }

}
