
package kz.iitu.springlab.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = "kz.iitu.springlab.account"
)
@EntityScan(
        basePackages = "kz.iitu.springlab.account"
)
@EnableJpaRepositories(
        basePackages = "kz.iitu.springlab.account"
)
public class Lab9Application {

    public static void main(String[] args) {
        SpringApplication.run(
                Lab9Application.class,
                args
        );
    }
}
