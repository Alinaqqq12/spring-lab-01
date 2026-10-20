
package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import java.time.Instant;

public record Book(
        Long id,
        String title,
        String author,
        int year,
        Genre genre,
        Instant createdAt
) {

    // Совместимость с лабораторными №5 и №6
    public Book(Long id, String title, String author, int year) {
        this(id, title, author, year, Genre.FICTION, Instant.now());
    }
}
