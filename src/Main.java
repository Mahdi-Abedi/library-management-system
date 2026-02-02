import entities.Library;
import entities.items.*;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 7 DEMO ===\n");

        // ۱. ایجاد کتابخانه
        Library library = new Library();

        System.out.println("1. Creating library items...");

        LibraryItem book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        ((Book) book).setPublicationYear(2018);
        ((Book) book).setPageCount(416);

        LibraryItem magazine = new Magazine("JAVA-2024-01", "Java Monthly",
                LocalDate.of(2024, 1, 15));
        ((Magazine) magazine).setPublisher("Java Publications Inc.");

        LibraryItem dvd = new DVD("DVD-001", "Java Design Patterns", "John Doe");
        ((DVD) dvd).setDurationMinutes(120);
        ((DVD) dvd).setGenre(MovieGenre.EDUCATIONAL);

        LibraryItem referenceBook = new ReferenceBook("REF-001", "Java Language Specification",
                "Programming Languages");

        library.addItem(book);
        library.addItem(magazine);
        library.addItem(dvd);
        library.addItem(referenceBook);

        System.out.println("\n2. Creating members...");
        Member ali = new Member(101, "Ali Rezaei", "ali@example.com");
        ali.setPhoneNumber("09123456789");
        ali.setStatus(MemberStatus.ACTIVE);

        Member sara = new Member(102, "Sara Mohammadi", "sara@example.com");
        sara.setStatus(MemberStatus.ACTIVE);

        library.addMember(ali);
        library.addMember(sara);

        System.out.println("\n3. Testing Records...");
        var stats = library.getStats().generateStatistics();
        System.out.println("Library Statistics:");
        System.out.println("  Total items: " + stats.totalItems());
        System.out.println("  Available: " + stats.availableItems());
        System.out.println("  Utilization: " + stats.getUtilizationPercentage() + "%");

        System.out.println("\n4. Testing Sealed Classes...");
        System.out.println("Book is a LibraryItem: " + (book instanceof LibraryItem));
        System.out.println("Magazine is a LibraryItem: " + (magazine instanceof LibraryItem));
        System.out.println("DVD is a LibraryItem: " + (dvd instanceof LibraryItem));
        System.out.println("ReferenceBook is a LibraryItem: " + (referenceBook instanceof LibraryItem));

        System.out.println("\n5. Testing canBeBorrowed() method...");
        System.out.println("Book can be borrowed: " + book.canBeBorrowed());
        System.out.println("Magazine can be borrowed: " + magazine.canBeBorrowed());
        System.out.println("DVD can be borrowed: " + dvd.canBeBorrowed());
        System.out.println("ReferenceBook can be borrowed: " + referenceBook.canBeBorrowed());

        System.out.println("\n6. Testing Enhanced Enums...");
        for (LibraryItemType type : LibraryItemType.values()) {
            System.out.println(type.getDisplayName() + ": " + type.getLoanInfo());
        }

        System.out.println("\n7. Testing Borrowing with BorrowingService...");
        if (book.canBeBorrowed()) {
            var borrowResult = library.borrowItem(book.getId(), ali);
            if (borrowResult.isSuccess()) {
                System.out.println("Successfully borrowed: " + book.getTitle());
                BorrowRecord record = borrowResult.getRecord();
                System.out.println("Due date: " + record.getDueDate());

                // تست Local Class در BorrowRecord
                System.out.println("\n8. Testing Local Class (ReportFormatter)...");
                System.out.println(record.generateReport());
            } else {
                System.out.println("Failed to borrow: " + borrowResult.getMessage());
            }
        }

        System.out.println("\n9. Testing Inner Class (Library.Statistics)...");
        var libraryStats = library.getStats();
        System.out.println("Total items: " + libraryStats.getTotalItems());
        System.out.println("Available items: " + libraryStats.getAvailableItems());
        System.out.println("Loanable items: " + libraryStats.getLoanableItems());

        System.out.println("Items by type:");
        libraryStats.getCountByType().forEach((type, count) -> {
            System.out.println("  " + type.getDisplayName() + ": " + count);
        });

        System.out.println("\n10. Testing search and filter...");
        List<LibraryItem> javaItems = library.searchItems("Java");
        System.out.println("Items with 'Java' in title: " + javaItems.size());

        List<LibraryItem> loanableItems = library.getLoanableItems();
        System.out.println("Currently loanable items: " + loanableItems.size());

        System.out.println("\n11. Testing Comparators...");
        if (!library.getAllItems().isEmpty()) {
            List<LibraryItem> sortedByTitle = library.getAllItems().stream()
                    .sorted((i1, i2) -> i1.getTitle().compareToIgnoreCase(i2.getTitle()))
                    .toList();
            System.out.println("First item alphabetically: " +
                    (sortedByTitle.isEmpty() ? "None" : sortedByTitle.get(0).getTitle()));
        }

        System.out.println("\n12. Testing item return...");
        boolean returned = library.returnItem(book.getId());
        System.out.println("Book returned successfully: " + returned);
        System.out.println("Book available after return: " + book.getAvailable());

        System.out.println("\n13. Final Library Report:");
        System.out.println(library.generateLibraryReport());

        System.out.println("\n14. Testing ReferenceBook (non-loanable)...");
        if (referenceBook.canBeBorrowed()) {
            var refBorrowResult = library.borrowItem(referenceBook.getId(), ali);
            System.out.println("Reference book borrow attempted: " +
                    (refBorrowResult.isSuccess() ? "SUCCESS (UNEXPECTED!)" : "FAILED (EXPECTED)"));
            if (!refBorrowResult.isSuccess()) {
                System.out.println("Expected error: " + refBorrowResult.getMessage());
            }
        } else {
            System.out.println("Correct: Reference book cannot be borrowed");
        }

        System.out.println("\n15. All Library Items:");
        library.getAllItems().forEach(item -> {
            System.out.println("  - " + item.getTitle() +
                    " (" + item.getItemType() + ")" +
                    " - Available: " + item.getAvailable() +
                    " - Loanable: " + item.canBeBorrowed());
        });

        System.out.println("\n=== CHAPTER 7 (BEYOND CLASSES) DEMO COMPLETED ===");
    }
}