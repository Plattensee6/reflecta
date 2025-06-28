package hu.test.reflecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "hu.test.reflecta.datasource.repository")
@EntityScan(basePackages = "hu.test.reflecta.datasource.entity")
public class ReflectaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReflectaApplication.class, args);
    }
}
