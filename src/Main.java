import entites.*;
import enums.MemberStatus;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM ===\n");

        Library library = new Library();

        System.out.println("Adding books to library...");
        int added = library.addMultipleBooks(
                new Book("978-0134685991", "Effective Java", "Joshua Bloch"),
                new Book("978-1617298299", "Spring in Action", "Craig Walls"),
                new Book("978-1492056271", "Java: The Complete Reference", "Herbert Schildt"),
                new Book("978-0596009205", "Head First Java", "Kathy Sierra"),
                new Book("978-0132350884", "Clean Code", "Robert C. Martin"),
                new Book("978-0201633610", "Design Patterns", "Erich Gamma"),
                new Book("978-0321356680", "Effective Java 2nd Edition", "Joshua Bloch"),
                new Book("978-0134685991", "Effective Java", "Joshua Bloch") // تکراری
        );
        System.out.println("Added " + added + " books (duplicates skipped)\n");

        Book effectiveJava = library.findBookByIsbn("978-0134685991");
        if (effectiveJava != null) {
            effectiveJava.setPublicationYear(2018);
            effectiveJava.setPageCount(416);
        }

        Book springInAction = library.findBookByIsbn("978-1617298299");
        if (springInAction != null) {
            springInAction.setPublicationYear(2021);
            springInAction.setPageCount(592);
        }

        Member ali = new Member(101, "Ali Rezaei", "ali@example.com");
        ali.setPhoneNumber("09123456789");

        Member sara = new Member(102, "Sara Mohammadi", "sara@example.com");
        sara.setPhoneNumber("09129876543");
        sara.setStatus(MemberStatus.ACTIVE);

        library.addMember(ali);
        library.addMember(sara);

        System.out.println("=== Borrowing Books ===");
        try {
            BorrowRecord record1 = library.borrowBook("978-0134685991", ali);
            System.out.println("Borrow record created:");
            System.out.println(record1);

            BorrowRecord record2 = library.borrowBook("978-1617298299", sara);
            System.out.println("\nSecond borrow record:");
            System.out.println(record2);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\n=== Search Operations ===");
        System.out.println("Search for 'Java':");
        library.searchBooks("Java").forEach(book ->
                System.out.println("  - " + book.getTitle())
        );

        System.out.println("\nSearch for 'Joshua':");
        library.searchBooks("Joshua").forEach(book ->
                System.out.println("  - " + book.getTitle())
        );

        System.out.println("\n=== Library Report ===");
        System.out.println(library.generateLibraryReport());

        System.out.println("=== Array Conversion ===");
        Book[] bookArray = library.getBooksAsArray();
        System.out.println("Converted to array with " + bookArray.length + " elements");

        System.out.println("\n=== Book Details (new toString) ===");
        System.out.println(effectiveJava);

        System.out.println("=== Returning a Book ===");
        boolean returned = library.returnBook("978-0134685991");
        System.out.println("Book returned: " + returned);
        System.out.println("\nUpdated availability: " + effectiveJava.getIsAvailable());

        System.out.println("\n=== ISBN Lookup ===");
        Book found = library.findBookByIsbn("978-0596009205");
        System.out.println("Found by ISBN: " + (found != null ? found.getTitle() : "Not found"));
    }
}