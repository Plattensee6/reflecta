package hu.test.reflecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "hu.test.reflecta")
@EnableJpaRepositories(basePackages = "hu.test.reflecta")
public class ReflectaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReflectaApplication.class, args);
    }
}
