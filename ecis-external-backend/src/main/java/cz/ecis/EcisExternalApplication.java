package cz.ecis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "cz.ecis")
@EnableAsync
public class EcisExternalApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcisExternalApplication.class, args);
	}

}
