package org.raul.fit_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.raul.fit_ai")
public class FitAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitAiApplication.class, args);
	}

}
