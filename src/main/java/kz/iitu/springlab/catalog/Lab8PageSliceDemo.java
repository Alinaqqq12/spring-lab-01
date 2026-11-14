
package kz.iitu.springlab.catalog;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Configuration
@Profile("dev")
public class Lab8PageSliceDemo {

    @Bean
    @Order(300)
    CommandLineRunner comparePageAndSlice(
            BookRepository repository,
            EntityManagerFactory entityManagerFactory
    ) {
        return args -> {

            SessionFactory sessionFactory =
                    entityManagerFactory.unwrap(SessionFactory.class);

            Statistics statistics = sessionFactory.getStatistics();
            statistics.setStatisticsEnabled(true);

            PageRequest pageable = PageRequest.of(
                    0, 3, Sort.by("year").descending()
            );

            System.out.println("===== PAGE VS SLICE =====");

            statistics.clear();

            Page<Book> page = repository.findAll(pageable);

            System.out.println(
                    "PAGE content: " + page.getNumberOfElements()
            );
            System.out.println(
                    "PAGE total: " + page.getTotalElements()
            );
            System.out.println(
                    "PAGE SQL statements: " +
                            statistics.getPrepareStatementCount()
            );

            statistics.clear();

            Slice<Book> slice =
                    repository.findSliceByTitleContainingIgnoreCase(
                            "", pageable
                    );

            System.out.println(
                    "SLICE content: " + slice.getNumberOfElements()
            );
            System.out.println(
                    "SLICE has next: " + slice.hasNext()
            );
            System.out.println(
                    "SLICE SQL statements: " +
                            statistics.getPrepareStatementCount()
            );

            System.out.println("===== PAGE VS SLICE COMPLETE =====");
        };
    }
}
