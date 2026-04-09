package com.pinelab.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CbsSimulatorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CbsSimulatorApplication.class, args);
    }
}