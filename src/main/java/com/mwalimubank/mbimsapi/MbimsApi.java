package com.mwalimubank.mbimsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mwalimubank.mbimsapi")
public class MbimsApi {

	public static void main(String[] args) {
		SpringApplication.run(MbimsApi.class, args);
	}

}
