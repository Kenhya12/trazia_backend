package com.trazia.trazia_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TraziaProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(TraziaProjectApplication.class, args);
    }
}
