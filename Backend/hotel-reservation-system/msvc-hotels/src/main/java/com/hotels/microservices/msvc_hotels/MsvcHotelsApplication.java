package com.hotels.microservices.msvc_hotels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcHotelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcHotelsApplication.class, args);
	}

}


