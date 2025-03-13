package com.applicantai.resumeoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the ApplicantAI Resume Optimizer.
 * Initializes the Spring Boot application with caching enabled.
 */
@SpringBootApplication
@EnableCaching
public class ResumeOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeOptimizerApplication.class, args);
    }
} 