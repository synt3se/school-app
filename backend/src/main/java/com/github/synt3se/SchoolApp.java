package com.github.synt3se;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SchoolApp {
    public static void main(String[] args) {
        SpringApplication.run(SchoolApp.class, args);
    }
}
