
package kz.iitu.springlab;

import kz.iitu.springlab.account.Lab9Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = "kz.iitu.springlab",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = Lab9Application.class
        )
)
@ConfigurationPropertiesScan(
        basePackages = "kz.iitu.springlab"
)
@EntityScan(
        basePackages = "kz.iitu.springlab"
)
@EnableJpaRepositories(
        basePackages = "kz.iitu.springlab"
)
public class SpringLab01Application {

    public static void main(String[] args) {
        SpringApplication.run(
                SpringLab01Application.class,
                args
        );
    }
}
