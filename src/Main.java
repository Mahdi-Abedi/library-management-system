import entities.Library;
import entities.items.*;
import entities.people.Member;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;
import interfaces.ItemFilter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 8 DEMO ===\n");

        Library library = new Library();

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

        Member ali = new Member(101, "Ali Rezaei", "ali@example.com");
        ali.setStatus(MemberStatus.ACTIVE);
        library.addMember(ali);

        System.out.println("1. Testing ItemFilter interface:");
        ItemFilter availableFilter = item -> item.getAvailable();
        List<LibraryItem> availableItems = library.findItems(availableFilter);
        System.out.println("Available items: " + availableItems.size());

        ItemFilter bookFilter = item -> item.getItemType() == LibraryItemType.BOOK;
        List<LibraryItem> books = library.findItems(bookFilter);
        System.out.println("Books: " + books.size());

        System.out.println("\n2. Testing ItemProcessor interface:");
        library.processItems(item ->
                System.out.println("  - " + item.getTitle()));

        System.out.println("\n3. Testing ItemTransformer interface:");
        List<String> itemDescriptions = library.transformItems(item ->
                String.format("%s (%s)", item.getTitle(), item.getItemType()));

        System.out.println("Item descriptions:");
        itemDescriptions.forEach(System.out::println);

        List<Map<String, Object>> itemMaps = library.transformItems(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", item.getTitle());
            map.put("type", item.getItemType());
            map.put("available", item.getAvailable());
            return map;
        });

        System.out.println("\nFirst item as Map: " + itemMaps.get(0));

        System.out.println("\n4. Testing Method References:");
        List<String> titles = library.transformItems(LibraryItem::getTitle);
        System.out.println("First title: " + titles.get(0));

        System.out.println("\n5. Testing complex lambdas:");
        library.processItems(item -> {
            if (item.getAvailable() && item.canBeBorrowed()) {
                System.out.println("✓ " + item.getTitle() + " - READY TO BORROW");
            }
        });

        System.out.println("\n6. Chaining operations:");
        long loanableCount = library.getAllItems().stream()
                .filter(LibraryItem::canBeBorrowed)
                .count();
        System.out.println("Loanable items: " + loanableCount);

        System.out.println("\n=== CHAPTER 8 (LAMBDAS) DEMO COMPLETED ===");
    }
}