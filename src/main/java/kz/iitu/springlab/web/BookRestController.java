
package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookPatchRequest;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookRequest;
import kz.iitu.springlab.web.dto.BookResponse;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Books",
        description = "Book catalogue REST API - Laboratory Work 7"
)
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;
    private final BookMapper mapper;

    public BookRestController(BookService service, BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // Previous labs: original collection endpoint
    @Operation(
            summary = "Get all books",
            description = "Returns books with optional author filtering and limit"
    )
    @ApiResponse(responseCode = "200", description = "Books retrieved")
    @GetMapping(params = "!page")
    public ResponseEntity<List<BookResponse>> findAll(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<BookResponse> books = mapper.toResponseList(
                service.findAll(author)
        );

        return ResponseEntity.ok(
                books.stream()
                        .limit(Math.max(0, limit))
                        .toList()
        );
    }

    // Lab 7, Variant 1: Pagination
    @Operation(
            summary = "Get books with pagination",
            description = "Returns the requested page, page size, total book count and content"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page or size",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping(params = "page")
    public ResponseEntity<Map<String, Object>> getBooksPage(
            @Parameter(description = "Page number starting from zero", example = "0")
            @RequestParam int page,

            @Parameter(description = "Number of books per page", example = "2")
            @RequestParam(defaultValue = "2") int size,

            @RequestParam(required = false) String author
    ) {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0"
            );
        }

        List<BookResponse> allBooks =
                mapper.toResponseList(service.findAll(author));

        long total = allBooks.size();
        long start = (long) page * size;

        List<BookResponse> content = start >= total
                ? List.of()
                : allBooks.subList(
                (int) start,
                (int) Math.min(start + size, total)
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("total", total);
        result.put("content", content);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Find a book by its identifier",
            description = "Returns one book or a ProblemDetail if the book does not exist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The book was found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this ID",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable Long id
    ) {
        Book book = service.requireById(id);

        return ResponseEntity.ok(mapper.toResponse(book));
    }

    @Operation(summary = "Create a new book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created with Location header"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "409", description = "Duplicate book")
    })
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request
    ) {
        Book created = service.create(mapper.toDomain(request));

        URI location = URI.create("/api/books/" + created.id());

        return ResponseEntity.created(location)
                .body(mapper.toResponse(created));
    }

    @Operation(summary = "Replace an existing book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate book")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> replace(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        Book updated = service.update(
                id,
                mapper.toDomain(request)
        );

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Operation(summary = "Delete a book")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // PATCH from previous laboratory work
    @Operation(
            summary = "Partially update a book",
            description = "Changes only the provided fields, preserving the others"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book partially updated"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate book")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> patch(
            @PathVariable Long id,
            @RequestBody BookPatchRequest patch
    ) {
        service.requireById(id);

        return service.patch(id, patch)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
