package com.example.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class Blps2Application {

    public static void main(String[] args) {
        SpringApplication.run(Blps2Application.class, args);
    }
}
