package com.testing.harness.tool;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSwagger2
@EnableAuthorizationServer
@EnableCaching
@EnableScheduling
public class TestingHarnessToolApplication implements CommandLineRunner {

	@Autowired
	DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(TestingHarnessToolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
	}

}
