
package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("dev")
public class Lab8QueryRunner {

    @Bean
    @Order(100)
    CommandLineRunner runLab8Queries(
            BookRepository repository,
            TransactionTemplate transactionTemplate
    ) {
        return args -> {

            System.out.println("===== LAB 8 QUERY TESTS =====");

            System.out.println("1. Books by genre:");
            System.out.println(
                    repository.findByGenre(Genre.FICTION).size()
            );

            System.out.println("2. Title contains Java:");
            System.out.println(
                    repository.findByTitleContainingIgnoreCase("Java").size()
            );

            System.out.println("3. Books between 2015 and 2025:");
            System.out.println(
                    repository.findByYearBetweenOrderByYearDesc(2015, 2025).size()
            );

            System.out.println("4. Top 3 newest fiction books:");
            System.out.println(
                    repository.findTop3ByGenreOrderByYearDesc(Genre.FICTION).size()
            );

            System.out.println("5. Books by author Joshua Bloch:");
            System.out.println(
                    repository.countByAuthorEntity_Name("Joshua Bloch")
            );

            System.out.println("6. JPQL search:");
            System.out.println(
                    repository.search(Genre.FICTION, 2020).size()
            );

            System.out.println("7. Publisher search (Variant 1):");
            System.out.println(
                    repository.findByPublisherName("Pearson").size()
            );

            System.out.println("8. Bulk archive before 2010:");
            Integer archived = transactionTemplate.execute(
                    status -> repository.archiveOlderThan(2010)
            );
            System.out.println("Archived books: " + archived);

            System.out.println("===== LAB 8 QUERY TESTS COMPLETE =====");
        };
    }
}
