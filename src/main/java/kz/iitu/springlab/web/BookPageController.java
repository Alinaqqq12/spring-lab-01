
package kz.iitu.springlab.web;

import jakarta.validation.groups.Default;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.validation.OnCreate;
import kz.iitu.springlab.validation.OnUpdate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String author,
            Model model
    ) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");
        return "books/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("editMode", false);
        return "books/form";
    }

    @GetMapping("/{bookId}/edit")
    public String editForm(
            @PathVariable("bookId") Long id,
            Model model
    ) {
        Book book = service.requireById(id);

        BookForm form = new BookForm();
        form.setId(book.id());
        form.setTitle(book.title());
        form.setAuthor(book.author());
        form.setYear(book.year());
        form.setGenre(Genre.FICTION);
        form.setAvailable(true);

        model.addAttribute("form", form);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("editMode", true);
        model.addAttribute("pathId", id);

        return "books/form";
    }

    @GetMapping("/{bookId}")
    public String bookDetails(
            @PathVariable("bookId") Long id,
            Model model
    ) {
        model.addAttribute("book", service.requireById(id));
        return "books/details";
    }

    @PostMapping
    public String create(
            @Validated({Default.class, OnCreate.class})
            @ModelAttribute("form") BookForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            model.addAttribute("editMode", false);
            return "books/form";
        }

        Book saved = service.create(
                new Book(
                        null,
                        form.getTitle(),
                        form.getAuthor(),
                        form.getYear()
                )
        );

        redirectAttributes.addFlashAttribute(
                "message",
                "Book '" + saved.title() + "' has been added"
        );

        return "redirect:/books";
    }

    @PostMapping("/{bookId}/edit")
    public String update(
            @PathVariable("bookId") Long id,
            @Validated({Default.class, OnUpdate.class})
            @ModelAttribute("form") BookForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (form.getId() != null && !form.getId().equals(id)) {
            binding.rejectValue(
                    "id",
                    "book.id.mismatch",
                    "Book ID does not match the URL"
            );
        }

        if (binding.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            model.addAttribute("editMode", true);
            model.addAttribute("pathId", id);
            return "books/form";
        }

        service.requireById(id);

        Book saved = service.save(
                new Book(
                        id,
                        form.getTitle(),
                        form.getAuthor(),
                        form.getYear()
                )
        );

        redirectAttributes.addFlashAttribute(
                "message",
                "Book '" + saved.title() + "' has been updated"
        );

        return "redirect:/books";
    }
}
