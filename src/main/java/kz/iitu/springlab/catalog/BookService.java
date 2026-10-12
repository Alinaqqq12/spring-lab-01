
package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        List<Book> books = repository.findAll();

        if (author == null || author.isBlank()) {
            return books;
        }

        return books.stream()
                .filter(book -> book.author()
                        .toLowerCase()
                        .contains(author.toLowerCase()))
                .toList();
    }

    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    public Book requireById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        return repository.save(
                new Book(
                        null,
                        book.title(),
                        book.author(),
                        book.year()
                )
        );
    }

    public Book save(Book book) {
        return repository.save(book);
    }

    public boolean delete(Long id) {
        return repository.deleteById(id);
    }

    public Optional<Book> patch(Long id, BookPatchRequest patch) {
        return repository.findById(id).map(existingBook -> {

            String newTitle = patch.title() != null
                    ? patch.title()
                    : existingBook.title();

            String newAuthor = patch.author() != null
                    ? patch.author()
                    : existingBook.author();

            int newYear = patch.year() != null
                    ? patch.year()
                    : existingBook.year();

            return repository.save(
                    new Book(id, newTitle, newAuthor, newYear)
            );
        });
    }
}
