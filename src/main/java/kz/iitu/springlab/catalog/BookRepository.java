
package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Lab 8: Five derived queries

    List<Book> findByGenre(Genre genre);

    List<Book> findByTitleContainingIgnoreCase(String fragment);

    List<Book> findByYearBetweenOrderByYearDesc(int from, int to);

    List<Book> findTop3ByGenreOrderByYearDesc(Genre genre);

    long countByAuthorEntity_Name(String name);

    // Compatibility with earlier work
    long countByAuthor(String author);

    // JPQL search
    @Query("""
            select b from Book b
            where b.genre = :genre
              and b.year >= :year
            order by b.title
            """)
    List<Book> search(
            @Param("genre") Genre genre,
            @Param("year") int year
    );

    // JPQL bulk update
    @Modifying(clearAutomatically = true)
    @Query("""
            update Book b
            set b.archived = true
            where b.year < :year
            """)
    int archiveOlderThan(@Param("year") int year);

    // Database pagination
    Page<Book> findByGenre(Genre genre, Pageable pageable);

    // Slice query - no total count required
    Slice<Book> findSliceByTitleContainingIgnoreCase(
            String fragment,
            Pageable pageable
    );

    // Fetch join to solve N+1
    @Query("""
            select b from Book b
            join fetch b.authorEntity
            """)
    List<Book> findAllWithAuthor();

    // Individual Variant 1: Publisher
    List<Book> findByPublisherName(String name);
}
