
package kz.iitu.springlab.catalog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Profile("dev")
public class Lab8DataInitializer {

    @Bean
    @Order(1)
    CommandLineRunner initializeLab8(
            AuthorRepository authorRepository,
            TagRepository tagRepository,
            PublisherRepository publisherRepository,
            BookRepository bookRepository
    ) {
        return args -> {

            // Avoid creating duplicates
            if (bookRepository.count() > 0) {
                System.out.println(
                        "LAB 8: Test data already exists."
                );
                return;
            }

            // Authors
            Author joshua = authorRepository.save(
                    new Author("Joshua Bloch", "USA")
            );

            Author robert = authorRepository.save(
                    new Author("Robert Martin", "USA")
            );

            Author craig = authorRepository.save(
                    new Author("Craig Walls", "USA")
            );

            // Tags
            Tag java = tagRepository.save(new Tag("Java"));
            Tag spring = tagRepository.save(new Tag("Spring"));
            Tag programming = tagRepository.save(
                    new Tag("Programming")
            );
            Tag software = tagRepository.save(
                    new Tag("Software Engineering")
            );

            // Publishers - Individual Variant 1
            Publisher pearson = publisherRepository.save(
                    new Publisher("Pearson", "USA")
            );

            Publisher manning = publisherRepository.save(
                    new Publisher("Manning", "USA")
            );

            Publisher oreilly = publisherRepository.save(
                    new Publisher("O'Reilly Media", "USA")
            );

            // Book 1
            Book b1 = new Book(
                    null, "Effective Java",
                    "Joshua Bloch", 2018
            );
            joshua.addBook(b1);
            b1.setPublisher(pearson);
            b1.addTag(java);
            b1.addTag(programming);

            // Book 2
            Book b2 = new Book(
                    null, "Clean Code",
                    "Robert Martin", 2008
            );
            robert.addBook(b2);
            b2.setPublisher(pearson);
            b2.addTag(software);

            // Book 3
            Book b3 = new Book(
                    null, "Spring in Action",
                    "Craig Walls", 2022
            );
            craig.addBook(b3);
            b3.setPublisher(manning);
            b3.addTag(spring);
            b3.addTag(java);

            // Book 4
            Book b4 = new Book(
                    null, "Java Fundamentals",
                    "Joshua Bloch", 2016
            );
            joshua.addBook(b4);
            b4.setPublisher(oreilly);
            b4.addTag(java);

            // Book 5
            Book b5 = new Book(
                    null, "Advanced Java",
                    "Joshua Bloch", 2020
            );
            joshua.addBook(b5);
            b5.setPublisher(pearson);
            b5.addTag(programming);

            // Book 6
            Book b6 = new Book(
                    null, "Clean Architecture",
                    "Robert Martin", 2017
            );
            robert.addBook(b6);
            b6.setPublisher(pearson);
            b6.addTag(software);

            // Book 7
            Book b7 = new Book(
                    null, "Spring Boot Guide",
                    "Craig Walls", 2023
            );
            craig.addBook(b7);
            b7.setPublisher(manning);
            b7.addTag(spring);

            // Book 8
            Book b8 = new Book(
                    null, "REST API Development",
                    "Craig Walls", 2024
            );
            craig.addBook(b8);
            b8.setPublisher(oreilly);
            b8.addTag(programming);

            // Save all books to PostgreSQL
            bookRepository.saveAll(
                    List.of(b1, b2, b3, b4, b5, b6, b7, b8)
            );

            System.out.println(
                    "LAB 8: Test data initialized successfully!"
            );
        };
    }
}
