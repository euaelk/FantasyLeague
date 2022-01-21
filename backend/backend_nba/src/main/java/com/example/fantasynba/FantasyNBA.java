package com.example.fantasynba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;

@SpringBootApplication
@RestController
@EnableAsync
public class FantasyNBA {

	public static void main(String[] args) {
		SpringApplication.run(FantasyNBA.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("NBA Games Lookup");
		executor.initialize();
		return executor;
	}

}
