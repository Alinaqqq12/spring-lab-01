package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookPatchRequest;
import kz.iitu.springlab.catalog.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    // GET /api/books?author=...&limit=10
    @GetMapping
    public ResponseEntity<List<Book>> findAll(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Book> books = service.findAll(author)
                .stream()
                .limit(limit)
                .toList();

        return ResponseEntity.ok(books);
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book created = service.create(book);

        URI location = URI.create("/api/books/" + created.id());

        return ResponseEntity
                .created(location)
                .body(created);
    }

    // PUT /api/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(
            @PathVariable Long id,
            @RequestBody Book book
    ) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book replacement = new Book(
                id,
                book.title(),
                book.author(),
                book.year()
        );

        Book saved = service.save(replacement);

        return ResponseEntity.ok(saved);
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // PATCH /api/books/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Book> patch(
            @PathVariable Long id,
            @RequestBody BookPatchRequest patch
    ) {
        return service.patch(id, patch)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}