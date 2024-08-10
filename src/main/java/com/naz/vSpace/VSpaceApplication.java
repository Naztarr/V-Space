package com.naz.vSpace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VSpaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VSpaceApplication.class, args);
	}

}
