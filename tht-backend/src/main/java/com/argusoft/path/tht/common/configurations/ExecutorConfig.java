package com.argusoft.path.tht.common.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean
    public ExecutorService executorService() {

        return Executors.newCachedThreadPool(); // You can adjust the thread pool configuration as needed
    }
}
