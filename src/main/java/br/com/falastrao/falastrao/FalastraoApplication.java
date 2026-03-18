package br.com.falastrao.falastrao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FalastraoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FalastraoApplication.class, args);
	}

}
