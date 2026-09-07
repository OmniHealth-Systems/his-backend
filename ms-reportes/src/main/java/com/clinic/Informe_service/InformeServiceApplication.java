package com.clinic.Informe_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class InformeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InformeServiceApplication.class, args);
	}

}
