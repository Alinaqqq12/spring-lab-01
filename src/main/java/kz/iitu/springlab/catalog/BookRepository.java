package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BookRepository {

    private final ConcurrentHashMap<Long, Book> books =
            new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong();

    public BookRepository() {
        save(new Book(null, "Effective Java", "Joshua Bloch", 2018));
        save(new Book(null, "Clean Code", "Robert Martin", 2008));
        save(new Book(null, "Spring in Action", "Craig Walls", 2022));
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public Book save(Book book) {
        Long id = book.id();

        if (id == null) {
            id = sequence.incrementAndGet();
        }

        Book savedBook = new Book(
                id,
                book.title(),
                book.author(),
                book.year()
        );

        books.put(id, savedBook);

        return savedBook;
    }

    public boolean deleteById(Long id) {
        return books.remove(id) != null;
    }
}