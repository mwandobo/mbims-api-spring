package com.mwalimubank.mbimsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.mwalimubank.mbimsapi")
@EnableScheduling
@EnableAsync
public class MbimsApi {

	public static void main(String[] args) {
		SpringApplication.run(MbimsApi.class, args);
	}

}
