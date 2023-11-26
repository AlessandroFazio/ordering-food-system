package github.alessandrofazio.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"github.alessandrofazio.dataaccess", "github.alessandrofazio.customer.service.dataaccess"})
@EntityScan(basePackages = {"github.alessandrofazio.dataaccess", "github.alessandrofazio.customer.service.dataaccess"})
@SpringBootApplication(scanBasePackages = "github.alessandrofazio")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
