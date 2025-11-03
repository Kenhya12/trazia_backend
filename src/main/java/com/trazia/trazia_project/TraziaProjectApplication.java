package com.trazia.trazia_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.trazia.trazia_project.config.TestSecurityConfig;

@SpringBootApplication
@EnableCaching
@ComponentScan(excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = TestSecurityConfig.class
    ))
    public class TraziaProjectApplication {

        public static void main(String[] args) {
            SpringApplication.run(TraziaProjectApplication.class, args);
        }

    }
