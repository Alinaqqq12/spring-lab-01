
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Tag(
        name = "Books",
        description = "Book catalogue REST API - Labs 7 and 8"
)
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "year", "genre", "createdAt");

    private final BookService service;
    private final BookMapper mapper;

    public BookRestController(
            BookService service,
            BookMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    // Previous laboratories: original collection endpoint
    @Operation(
            summary = "Get all books",
            description = "Original endpoint with optional author filtering"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
    )
    @GetMapping(params = "!page")
    public ResponseEntity<List<BookResponse>> findAll(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<BookResponse> books =
                mapper.toResponseList(service.findAll(author));

        return ResponseEntity.ok(
                books.stream()
                        .limit(Math.max(0, limit))
                        .toList()
        );
    }

    // Lab 08: PostgreSQL pagination and sorting
    @Operation(
            summary = "Get books with database pagination",
            description = "Pagination and sorting using Spring Data JPA and PostgreSQL"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page returned successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping(params = "page")
    public ResponseEntity<Page<BookResponse>> getBooksPage(
            @Parameter(
                    description = "Page number, starting from zero",
                    example = "0"
            )
            @RequestParam int page,

            @Parameter(
                    description = "Number of books on one page",
                    example = "3"
            )
            @RequestParam(defaultValue = "5") int size,

            @Parameter(
                    description = "Sorting field and direction",
                    example = "year,desc"
            )
            @RequestParam(defaultValue = "title,asc") String sort,

            @RequestParam(required = false) Genre genre
    ) {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0"
            );
        }

        String[] sortParts = sort.split(",", -1);

        if (sortParts.length > 2
                || sortParts[0].isBlank()
                || !ALLOWED_SORT_FIELDS.contains(sortParts[0])) {
            throw new IllegalArgumentException(
                    "Invalid sorting field: " + sort
            );
        }

        String direction = sortParts.length == 2
                ? sortParts[1]
                : "asc";

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "Sort direction must be asc or desc"
            );
        }

        Sort sorting = Sort.by(
                Sort.Direction.fromString(direction),
                sortParts[0]
        );

        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        Page<BookResponse> result = service.find(genre, pageable)
                .map(mapper::toResponse);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Find a book by its identifier",
            description = "Returns a book or a 404 ProblemDetail"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The book was found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(
            @Parameter(
                    description = "Book identifier",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        Book book = service.requireById(id);
        return ResponseEntity.ok(mapper.toResponse(book));
    }

    @Operation(summary = "Create a new book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created with Location header"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid book data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate book"
            )
    })
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request
    ) {
        Book created = service.create(
                mapper.toDomain(request)
        );

        URI location = URI.create(
                "/api/books/" + created.id()
        );

        return ResponseEntity.created(location)
                .body(mapper.toResponse(created));
    }

    @Operation(summary = "Replace an existing book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid book data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate book"
            )
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

        return ResponseEntity.ok(
                mapper.toResponse(updated)
        );
    }

    @Operation(summary = "Delete a book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found"
            )
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

    // PATCH functionality from Lab 6
    @Operation(
            summary = "Partially update a book",
            description = "Only the supplied fields are updated"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book partially updated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate book"
            )
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
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }
}
