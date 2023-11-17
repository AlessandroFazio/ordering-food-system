package github.alessandrofazio.restaurant.service.domain;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"github.alessandrofazio.restaurant.service.dataaccess", "github.alessandrofazio.dataaccess.restaurant"})
@EntityScan(basePackages = {"github.alessandrofazio.restaurant.service.dataaccess", "github.alessandrofazio.dataaccess.restaurant"})
@SpringBootApplication(scanBasePackages = "github.alessandrofazio")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
