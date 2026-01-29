package entites;

import exceptions.BookNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {
    private final static int MAX_REPORT_BOOK_ITEMS = 5;

    List<Book> books;
    List<Member> members;
    private List<BorrowRecord> borrowRecords;

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.borrowRecords = new ArrayList<>();
    }

    public boolean addBook(Book book) {
        if (book == null || book.getIsbn() == null)
            return false;

        boolean exists = books.stream().anyMatch(b -> b.equals(book));
        if (exists)
            return false;

        return this.books.add(book);
    }

    public boolean addMember(Member member) {
        if (member == null)
            return false;

        return members.add(member);
    }

    public int addMultipleBooks(Book... books) {
        if (books == null || books.length == 0)
            return 0;

        int total = 0;
        for (Book book : books) {
            if (addBook(book))
                total++;
        }
        return total;
    }

    public boolean removeBook(String isbn) {
        return this.books.removeIf(b -> b.getIsbn().equals(isbn));
    }

    public Book searchBook(String title, String author) {
        if (books == null || books.isEmpty())
            return null;

        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()) &&
                        book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .findFirst().orElse(null);
    }

    public List<Book> findAllBookByAuthor(String author) {
        if (books == null || books.isEmpty() || author == null || author.isBlank())
            return null;
        return books.stream()
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public Optional<Book> findBookByIsbnOptional(String isbn) {
        if (books == null || books.isEmpty() || isbn == null || isbn.isBlank())
            return Optional.empty();

        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();

    }

    public Optional<BorrowRecord> findActiveBorrowRecord(String isbn) {
        if (borrowRecords == null || borrowRecords.isEmpty() || isbn == null || isbn.isBlank())
            return Optional.empty();

        return borrowRecords.stream()
                .filter(borrowRecord -> borrowRecord.getBook().getIsbn().equals(isbn) &&
                        borrowRecord.getReturnDate() == null)
                .findFirst();
    }

    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(Book::getIsAvailable)
                .collect(Collectors.toList());
    }

    public String generateLibraryReport() {
        long bookCount = books.size();
        long borrowedCount = getAvailableBooks().size();

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(50)).append("\n");
        report.append("LIBRARY REPORT\n");
        report.append("=".repeat(50)).append("\n\n");

        report.append(String.format("Total Books: %d%n", books.size()));
        report.append(String.format("Available: %d%n", getAvailableBooks().size()));
        report.append(String.format("Borrowed: %d%n", books.size() - getAvailableBooks().size()));

        report.append("\n\tLastest 5 book: ");
        books.stream().limit(MAX_REPORT_BOOK_ITEMS)
                .forEach(book -> report.append(String.format("  • %s by %s\n",
                        book.getTitle(), book.getAuthor())));

        List<BorrowRecord> overdue = borrowRecords.stream()
                .filter(BorrowRecord::isOverdue)
                .toList();

        if (!overdue.isEmpty()) {
            report.append("OVERDUE BOOKS:\n");
            report.append("-".repeat(40)).append("\n");
            overdue.forEach(record ->
                    report.append(String.format("  • %s - Due: %s (Overdue: %d days)\n",
                            record.getBook().getTitle(),
                            record.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                            record.getDaysOverdue()))
            );
            report.append("\n");
        }

        return report.toString();
    }

    public List<Book> searchBooks(String keyword) {
        if (books == null || books.isEmpty() || keyword == null || keyword.isBlank())
            return null;

        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getIsbn().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Book getBookByIsbnOrThrow(String isbn) {
        return findBookByIsbnOptional(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found"));
    }

    public Book[] getBooksAsArray() {
        return books.toArray(new Book[0]);
    }

    public BorrowRecord borrowBook(String isbn, Member member) {
        if (isbn == null || isbn.isBlank() || member == null)
            return null;

        Optional<Book> bookOptional = findBookByIsbnOptional(isbn);
        if (bookOptional.isEmpty() || !bookOptional.get().getIsAvailable())
            return null;

        BorrowRecord borrowRecord = new BorrowRecord(bookOptional.get(), member);
        borrowRecords.add(borrowRecord);

        return borrowRecord;
    }

    public BorrowRecord borrowBook(String isbn) {
        if (members.isEmpty())
            throw new IllegalStateException("No members available");

        return borrowBook(isbn, members.getFirst());
    }

    public BorrowRecord borrowBook(Book book, Member member) {
        if (book == null)
            throw new IllegalArgumentException("Book cannot be null");
        return borrowBook(book.getIsbn(), member);

    }

    public BorrowRecord borrowBook(String isbn, Member member, int days) {
        if (isbn == null || isbn.isBlank() || member == null)
            return null;

        if (days <= 0)
            throw new IllegalArgumentException("Borrow days must be positive");

        if (days > 30)
            throw new IllegalArgumentException("Maximum borrow period is 30 days");


        Optional<Book> bookOptional = findBookByIsbnOptional(isbn);
        if (bookOptional.isEmpty() || !bookOptional.get().getIsAvailable())
            return null;

        BorrowRecord borrowRecord = new BorrowRecord(bookOptional.get(), member);
        borrowRecord.setDueDate(LocalDate.now().plusDays(days));
        borrowRecords.add(borrowRecord);

        return borrowRecord;

    }

    public int borrowMultipleBooks(Member member, String... isbns) {
        if (member == null || isbns == null || isbns.length == 0)
            return 0;
        int total = 0;
        for (String isbn : isbns) {
            BorrowRecord borrowRecord = borrowBook(isbn, member);
            if (borrowRecord != null)
                total++;
        }

        return total;
    }

    public int borrowMultipleBooks(Member member, Book... books) {
        if (member == null || books == null || books.length == 0)
            return 0;
        int total = 0;
        for (Book book : books) {
            BorrowRecord borrowRecord = borrowBook(book, member);
            if (borrowRecord != null)
                total++;
        }

        return total;
    }

    public boolean returnBook(String isbn) {
        if (isbn == null || isbn.isBlank())
            return false;

        return findActiveBorrowRecord(isbn)
                .map(borrowRecord -> {
                    borrowRecord.returnBook();
                    return true;
                })
                .orElse(false);
    }

    public int returnMultipleBooks(String... isbns) {
        if (isbns == null || isbns.length == 0)
            return 0;

        int total = 0;
        for (String isbn : isbns) {
            if (returnBook(isbn))
                total++;
        }

        return total;
    }

}
