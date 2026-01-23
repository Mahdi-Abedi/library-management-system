package entites;

import enums.BookStatus;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    private int publicationYear;
    private int pageCount;
    private BookStatus status;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        isAvailable = true;
        this.status = BookStatus.AVAILABLE;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void borrowBook() {
        isAvailable = false;
        status = BookStatus.BORROWED;
    }

    public void returnBook() {
        isAvailable = true;
        status = BookStatus.AVAILABLE;
    }

    @Override
    public int hashCode() {
        return isbn != null ? isbn.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Book book = (Book) obj;
        return isbn != null ? book.getIsbn().equals(isbn) : book.getIsbn() == null;
    }

    @Override
    public String toString() {
        return new StringBuilder("Book Details:")
                .append("\n\tTitle: ").append(title)
                .append("\n\tAuthor: ").append(author)
                .append("\n\tISBN: ").append(isbn)
                .append("\n\tStatus: ").append(status)
                .append("\n\tAvailable: ").append(isAvailable)
                .append("\n\tPublication Year: ").append(publicationYear)
                .append("\n\tPages: ").append(pageCount)
                .toString();
    }
}