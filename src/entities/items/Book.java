package entities.items;

import enums.BookStatus;
import enums.LibraryItemType;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

public class Book extends LibraryItem implements LoanPolicy, ReservationPolicy {
    private String isbn;
    private String author;
    private int publicationYear;
    private int pageCount;
    private BookStatus status;

    public Book(String isbn, String title, String author) {
        super(LibraryItem.generateId(LibraryItemType.BOOK, isbn), title);
        this.isbn = isbn;
        this.author = author;
        this.status = BookStatus.AVAILABLE;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
        borrowItem();
        status = BookStatus.BORROWED;
    }

    public void returnBook() {
        returnItem();
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
    public LibraryItemType getItemType() {
        return LibraryItemType.BOOK;
    }

    @Override
    public int getMaxLoanDays() {
        return 14;
    }

    @Override
    public double getDailyFine() {
        return 500d;
    }

    @Override
    public boolean isRenewable() {
        return true;
    }

    @Override
    public int getMaxRenewals() {
        return 1;
    }

    @Override
    public int getMaxReservationDays() {
        return 3;
    }

    @Override
    public int getMaxSimultaneousReservations() {
        return 2;
    }

    @Override
    public String toString() {
        return new StringBuilder("Book Details:")
                .append("\n\tTitle: ").append(getTitle())
                .append("\n\tAuthor: ").append(author)
                .append("\n\tISBN: ").append(isbn)
                .append("\n\tStatus: ").append(status)
                .append("\n\tAvailable: ").append(getAvailable())
                .append("\n\tPublication Year: ").append(publicationYear)
                .append("\n\tPages: ").append(pageCount)
                .toString();
    }
}