package com.example.fantasynba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;

@SpringBootApplication
@RestController
@EnableAsync
public class FantasyNBA {

	public static void main(String[] args) {
		SpringApplication.run(FantasyNBA.class, args);
	}

	@Bean(name = "threadPoolForGames")
	public Executor taskExecutor1() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Getting NBA Game Data Asynchronously");
		executor.initialize();
		return executor;
	}

	@Bean(name = "threadPoolForStats")
	public Executor taskExecutor2() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("Getting NBA Stats Data Asynchronously");
		executor.initialize();
		return executor;
	}
}
