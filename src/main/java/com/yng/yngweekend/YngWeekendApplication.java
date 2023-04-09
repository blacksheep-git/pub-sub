package com.yng.yngweekend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YngWeekendApplication {
	public static void main(String[] args) {
		SpringApplication.run(YngWeekendApplication.class, args);
	}

}
