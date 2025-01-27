package com.module.idw_signage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.module.idw_signage")
@EnableJpaRepositories(basePackages = "com.module.idw_signage.repository")
public class IdwSignageApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdwSignageApplication.class, args);
	}

}
