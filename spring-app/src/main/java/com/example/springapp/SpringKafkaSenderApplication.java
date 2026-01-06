package com.example.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringKafkaSenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaSenderApplication.class, args);
    }
}
