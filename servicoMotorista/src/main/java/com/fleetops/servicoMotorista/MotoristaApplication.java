package com.fleetops.servicoMotorista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MotoristaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotoristaApplication.class, args);
    }
}
