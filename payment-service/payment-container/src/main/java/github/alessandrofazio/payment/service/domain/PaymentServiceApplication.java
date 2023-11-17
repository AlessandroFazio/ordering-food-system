package github.alessandrofazio.payment.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "github.alessandrofazio.payment.dataaccess")
@EntityScan(basePackages = "github.alessandrofazio.payment.dataaccess")
@SpringBootApplication(scanBasePackages = "github.alessandrofazio")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
