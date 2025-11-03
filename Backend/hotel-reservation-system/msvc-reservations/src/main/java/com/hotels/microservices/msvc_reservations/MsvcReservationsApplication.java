package com.hotels.microservices.msvc_reservations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;


@SpringBootApplication
@EnableMongoAuditing
@EnableFeignClients
@EnableDiscoveryClient
public class MsvcReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcReservationsApplication.class, args);
	}

}
