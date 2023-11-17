package github.alessandrofazio.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"github.alessandrofazio.order.data.access", "github.alessandrofazio.dataaccess"})
@EntityScan(basePackages = {"github.alessandrofazio.order.data.access", "github.alessandrofazio.dataaccess"})
@SpringBootApplication(scanBasePackages = "github.alessandrofazio")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
