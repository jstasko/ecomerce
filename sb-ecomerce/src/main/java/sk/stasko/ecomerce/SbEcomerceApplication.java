package sk.stasko.ecomerce;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
		info = @Info(
				title = "Ecommerce REST API Documentation",
				description = "Ecommerce Demo RESP API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Jozef Stasko",
						email = "stasko.jozef427@gmail.com"
				),
				license = @License(
						name = "Apache 2.0"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Ecommerce RESP API Documentation"
		)
)
public class SbEcomerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbEcomerceApplication.class, args);
	}

}
