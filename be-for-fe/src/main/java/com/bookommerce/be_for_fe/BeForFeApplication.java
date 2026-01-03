package com.bookommerce.be_for_fe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.bookommerce.be_for_fe.entity")
public class BeForFeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeForFeApplication.class, args);
	}

}
