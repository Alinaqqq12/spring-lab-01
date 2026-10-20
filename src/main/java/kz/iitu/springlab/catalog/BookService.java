
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

    private void checkDuplicate(Book book, Long excludeId) {
        boolean duplicate = repository.findAll().stream()
                .anyMatch(existing ->
                        !existing.id().equals(excludeId)
                                && existing.title().equalsIgnoreCase(book.title())
                                && existing.author().equalsIgnoreCase(book.author())
                );

        if (duplicate) {
            throw new DuplicateTitleException(book.title());
        }
    }

    public Book create(Book book) {
        checkDuplicate(book, null);

        return repository.save(
                new Book(
                        null,
                        book.title(),
                        book.author(),
                        book.year(),
                        book.genre(),
                        book.createdAt()
                )
        );
    }

    public Book save(Book book) {
        return repository.save(book);
    }

    public Book update(Long id, Book book) {
        Book existing = requireById(id);

        checkDuplicate(book, id);

        return repository.save(
                new Book(
                        id,
                        book.title(),
                        book.author(),
                        book.year(),
                        book.genre(),
                        existing.createdAt()
                )
        );
    }

    public boolean delete(Long id) {
        return repository.deleteById(id);
    }

    // PATCH from previous laboratory work
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

            Book updated = new Book(
                    id,
                    newTitle,
                    newAuthor,
                    newYear,
                    existingBook.genre(),
                    existingBook.createdAt()
            );

            checkDuplicate(updated, id);

            return repository.save(updated);
        });
    }
}
