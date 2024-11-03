package br.com.poofinal.bands_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BandsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BandsApiApplication.class, args);
	}

}
