package entities.items;

import enums.LibraryItemType;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

import java.time.LocalDate;

public final class Book extends LibraryItem implements LoanPolicy, ReservationPolicy {
    private String isbn;
    private String author;
    private int publicationYear;
    private int pageCount;

    public Book(String isbn, String title, String author) {
        super(LibraryItem.generateId(LibraryItemType.BOOK, isbn), title);
        this.isbn = isbn;
        this.author = author;
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
                .append("\n\tStatus: ").append(getStatus())
                .append("\n\tAvailable: ").append(getAvailable())
                .append("\n\tPublication Year: ").append(publicationYear)
                .append("\n\tPages: ").append(pageCount)
                .toString();
    }

    public static class Builder {
        private String isbn;
        private String title;
        private String author;
        private int publicationYear;
        private int pageCount;

        public Builder(String isbn, String title, String author) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
        }

        public Builder setPublicationYear(int publicationYear) {
            if (publicationYear > LocalDate.now().getYear() || publicationYear < 1800)
                throw new IllegalArgumentException("Invalid publication year");

            this.publicationYear = publicationYear;
            return this;
        }

        public Builder setPageCount(int pageCount) {
            if (pageCount <= 0)
                throw new IllegalArgumentException("Page count must be positive");

            this.pageCount = pageCount;
            return this;
        }

        public Book build() {
            Book book = new Book(isbn, title, author);
            book.setPublicationYear(publicationYear);
            book.setPageCount(pageCount);

            return book;
        }
    }
}