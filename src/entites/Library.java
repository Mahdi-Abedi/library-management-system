package entites;

import enums.BookStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    List<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }

    public boolean addBook(Book book) {
        if (book == null || book.getIsbn() == null)
            return false;

        boolean exists = books.stream().anyMatch(b -> b.equals(book));
        if (exists)
            return false;

        return this.books.add(book);
    }

    public boolean removeBook(String isbn) {
        return this.books.removeIf(b -> b.getIsbn().equals(isbn));
    }

    public Book searchBook(String title, String author) {
        if (books == null || books.isEmpty())
            return null;

        return books.stream()
                .filter(book -> book.getTitle().equals(title) && book.getAuthor().equals(author))
                .findFirst().orElse(null);
    }

    public List<Book> findAllBookByAuthor(String author) {
        if (books == null || books.isEmpty())
            return null;
        return books.stream()
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(Book::getIsAvailable)
                .collect(Collectors.toList());
    }
}
