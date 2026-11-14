
package kz.iitu.springlab.catalog;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("dev")
public class Lab8NPlusOneDemo {

    @Bean
    @Order(200)
    CommandLineRunner demonstrateNPlusOne(
            BookRepository repository,
            EntityManagerFactory entityManagerFactory,
            EntityManager entityManager,
            TransactionTemplate transactionTemplate
    ) {
        return args -> {

            SessionFactory sessionFactory =
                    entityManagerFactory.unwrap(SessionFactory.class);

            Statistics statistics = sessionFactory.getStatistics();
            statistics.setStatisticsEnabled(true);

            System.out.println("===== LAB 8 N+1 DEMO =====");

            // Test 1: without JOIN FETCH
            statistics.clear();

            transactionTemplate.executeWithoutResult(status -> {
                entityManager.clear();

                var books = repository.findAll();

                for (Book book : books) {
                    if (book.getAuthorEntity() != null) {
                        System.out.println(
                                book.getTitle() + " - " +
                                        book.getAuthorEntity().getName()
                        );
                    }
                }
            });

            long withoutFetch = statistics.getPrepareStatementCount();

            System.out.println(
                    "SQL statements WITHOUT JOIN FETCH: " + withoutFetch
            );

            // Test 2: with JOIN FETCH
            statistics.clear();

            transactionTemplate.executeWithoutResult(status -> {
                entityManager.clear();

                var books = repository.findAllWithAuthor();

                for (Book book : books) {
                    System.out.println(
                            book.getTitle() + " - " +
                                    book.getAuthorEntity().getName()
                    );
                }
            });

            long withFetch = statistics.getPrepareStatementCount();

            System.out.println(
                    "SQL statements WITH JOIN FETCH: " + withFetch
            );

            System.out.println("===== N+1 DEMO COMPLETE =====");
        };
    }
}
