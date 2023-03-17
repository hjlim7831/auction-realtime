package com.dokidoki.bid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BidApplication {
	public static void main(String[] args) {
		SpringApplication.run(BidApplication.class, args);
	}

}