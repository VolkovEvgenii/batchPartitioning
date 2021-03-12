package ru.volkov.batch.partitioning;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class PartitioningApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartitioningApplication.class, args);
    }

}
