package com.example.feedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FeedserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedserviceApplication.class, args);
    }
}
