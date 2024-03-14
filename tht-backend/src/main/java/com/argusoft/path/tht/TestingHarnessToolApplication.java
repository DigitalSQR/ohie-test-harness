package com.argusoft.path.tht;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

/**
 * CommandLineRunner.
 *
 * @author Dhruv
 */
@SpringBootApplication
@EnableSwagger2
@EnableAuthorizationServer
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@EnableMethodSecurity
public class TestingHarnessToolApplication implements CommandLineRunner {

    DataSource dataSource;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Value("${spring.flyway.locations}")
    String flywayLocation;

    public static void main(String[] args) {
        SpringApplication.run(TestingHarnessToolApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource)
                .locations(flywayLocation).
                load()
                .migrate();
    }

}
