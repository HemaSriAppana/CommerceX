package com.practice.CommerceX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.practice.CommerceX.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)

public class CommerceXApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommerceXApplication.class, args);
	}

}
