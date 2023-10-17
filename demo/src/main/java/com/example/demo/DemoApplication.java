package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.example.demo")
public class DemoApplication {
    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    @PostConstruct
    void postConstruct(){
        logger.info("Mongodb Url received- {}", mongodbUri);
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
