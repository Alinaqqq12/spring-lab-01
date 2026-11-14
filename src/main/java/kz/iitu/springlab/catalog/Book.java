
package kz.iitu.springlab.catalog;

import jakarta.persistence.*;
import kz.iitu.springlab.web.Genre;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    // Compatibility with Labs 5-7
    @Column(name = "author_name")
    private String author;

    @Column(name = "publication_year")
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre = Genre.FICTION;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Version
    private long version;

    // Lab 8: Many books can belong to one author
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author authorEntity;

    // Lab 8: Many-to-many relationship with tags
    @ManyToMany
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Lab 8, Variant 1: Publisher relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    // Constructor required by JPA
    protected Book() {
    }

    // Compatibility with Labs 5-6
    public Book(
            Long id,
            String title,
            String author,
            int year
    ) {
        this(
                id,
                title,
                author,
                year,
                Genre.FICTION,
                Instant.now()
        );
    }

    // Compatibility with Lab 7
    public Book(
            Long id,
            String title,
            String author,
            int year,
            Genre genre,
            Instant createdAt
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre != null
                ? genre
                : Genre.FICTION;
        this.createdAt = createdAt != null
                ? createdAt
                : Instant.now();
    }

    // Record-style methods for previous labs

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public int year() {
        return year;
    }

    public Genre genre() {
        return genre;
    }

    public Instant createdAt() {
        return createdAt;
    }

    // Standard JPA getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public Genre getGenre() {
        return genre;
    }

    public boolean isArchived() {
        return archived;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    public Author getAuthorEntity() {
        return authorEntity;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // Lab 8: Author relationship

    public void setAuthorEntity(Author authorEntity) {
        this.authorEntity = authorEntity;

        if (authorEntity != null) {
            this.author = authorEntity.getName();
        }
    }

    // Lab 8: Tag relationship

    public void addTag(Tag tag) {
        if (tag != null) {
            tags.add(tag);
            tag.getBooks().add(this);
        }
    }

    public void removeTag(Tag tag) {
        if (tag != null) {
            tags.remove(tag);
            tag.getBooks().remove(this);
        }
    }

    // Lab 8: Publisher relationship

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
