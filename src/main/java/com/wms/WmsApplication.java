package com.wms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class WmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WmsApplication.class, args);
	}

	// GEÇİCİ: Admin123! için BCrypt hash üret
	@Bean
	public CommandLineRunner printAdminPasswordHash(PasswordEncoder passwordEncoder) {
		return args -> {
			String raw = "Admin123!";
			String hash = passwordEncoder.encode(raw);
			System.out.println("==== BCrypt hash for Admin123! ====");
			System.out.println(hash);
			System.out.println("===================================");
		};
	}
}