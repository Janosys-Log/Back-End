package com.fleetops.motorista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class VeiculoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeiculoApplication.class, args);
    }
}
