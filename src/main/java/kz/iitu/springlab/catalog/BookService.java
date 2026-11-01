
package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(String author) {
        List<Book> books = repository.findAll();

        if (author == null || author.isBlank()) {
            return books;
        }

        return books.stream()
                .filter(book -> book.author() != null
                        && book.author().toLowerCase()
                        .contains(author.toLowerCase()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
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

    @Transactional
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

    @Transactional
    public Book save(Book book) {
        return repository.save(book);
    }

    @Transactional
    public Book update(Long id, Book book) {
        Book existing = requireById(id);
        checkDuplicate(book, id);

        existing.setTitle(book.title());
        existing.setAuthor(book.author());
        existing.setYear(book.year());
        existing.setGenre(book.genre());

        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    // PATCH from previous laboratory work
    @Transactional
    public Optional<Book> patch(Long id, BookPatchRequest patch) {
        return repository.findById(id).map(existing -> {

            String newTitle = patch.title() != null
                    ? patch.title()
                    : existing.title();

            String newAuthor = patch.author() != null
                    ? patch.author()
                    : existing.author();

            int newYear = patch.year() != null
                    ? patch.year()
                    : existing.year();

            Book updated = new Book(
                    id,
                    newTitle,
                    newAuthor,
                    newYear,
                    existing.genre(),
                    existing.createdAt()
            );

            checkDuplicate(updated, id);

            existing.setTitle(newTitle);
            existing.setAuthor(newAuthor);
            existing.setYear(newYear);

            return repository.save(existing);
        });
    }

    // Lab 08: Database pagination and sorting
    @Transactional(readOnly = true)
    public Page<Book> find(Genre genre, Pageable pageable) {
        if (genre == null) {
            return repository.findAll(pageable);
        }

        return repository.findByGenre(genre, pageable);
    }

    // Lab 08: Bulk archive operation
    @Transactional
    public int archiveOlderThan(int year) {
        return repository.archiveOlderThan(year);
    }
}
