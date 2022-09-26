package com.example.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.example.restservice.repository.ProductRepository;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class RestServiceApplication {

	@Autowired
	ProductRepository productRepository;
	public static void main(String[] args) {
		SpringApplication.run(RestServiceApplication.class, args);
	}
}
