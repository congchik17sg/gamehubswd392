package com.example.GameHub;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class GameHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameHubApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openSwagger() {
		String url = "http://localhost:8080/gamehub/swagger-ui/index.html";
		try {
			Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
