
package kz.iitu.springlab.catalog;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publisher")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String country;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books = new ArrayList<>();

    protected Publisher() {
    }

    public Publisher(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
        book.setPublisher(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setPublisher(null);
    }
}
