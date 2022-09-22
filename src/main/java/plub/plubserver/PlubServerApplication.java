package plub.plubserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class PlubServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlubServerApplication.class, args);
	}

}
