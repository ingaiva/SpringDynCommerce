package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "data.repositorys")
@EntityScan(basePackages = "data.entitys")
@SpringBootApplication
public class SpringCommercantSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCommercantSiteApplication.class, args);
	}

}
