package cz.ecis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"cz.ecis"})
@EnableAsync
public class EcisTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcisTestApplication.class, args);
	}

}
