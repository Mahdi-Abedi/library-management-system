import entities.Library;
import entities.items.*;
import entities.people.Member;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;
import services.ItemCatalog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 9 DEMO ===\n");

        Library library = new Library();

        Book effectiveJava = new Book.Builder("978-0134685991", "Effective Java", "Joshua Bloch")
                .setPublicationYear(2018)
                .setPageCount(416)
                .build();

        Book cleanCode = new Book.Builder("978-0132350884", "Clean Code", "Robert Martin")
                .setPublicationYear(2008)
                .setPageCount(464)
                .build();

        Magazine javaMag = new Magazine("JAVA-2024-01", "Java Monthly", LocalDate.of(2024, 1, 15));
        javaMag.setPublisher("Java Publications Inc.");

        DVD designPatterns = new DVD("DVD-001", "Java Design Patterns", "John Doe");
        designPatterns.setDurationMinutes(120);
        designPatterns.setGenre(MovieGenre.EDUCATIONAL);

        ReferenceBook javaSpec = new ReferenceBook("REF-001", "Java Language Specification", "Programming Languages");

        library.addItem(effectiveJava);
        library.addItem(cleanCode);
        library.addItem(javaMag);
        library.addItem(designPatterns);
        library.addItem(javaSpec);

        Member ali = new Member(101, "Ali Rezaei", "ali@example.com");
        ali.setStatus(MemberStatus.ACTIVE);
        library.addMember(ali);

        library.borrowItem(effectiveJava.getId(), ali);

        System.out.println("1. Generic Methods with Class<T>:");
        List<Book> allBooks = library.getItemsByType(Book.class);
        System.out.println("Total books found: " + allBooks.size());

        Optional<DVD> foundDVD = library.findItemByTypeAndId(DVD.class, "DVD-001");
        foundDVD.ifPresentOrElse(
                dvd -> System.out.println("DVD found: " + dvd.getTitle()),
                () -> System.out.println("DVD not found")
        );

        System.out.println("\n2. Collections Grouping:");
        Map<LibraryItemType, List<LibraryItem>> groupedByType = library.groupItemsByType();
        groupedByType.forEach((type, items) -> {
            if (!items.isEmpty()) {
                System.out.println(type.getDisplayName() + ": " + items.size() + " items");
            }
        });

        System.out.println("\n3. Partitioning by Availability:");
        Map<Boolean, List<LibraryItem>> availabilityMap = library.partitionByAvailability();
        System.out.println("Available items: " + availabilityMap.get(true).size());
        System.out.println("Borrowed items: " + availabilityMap.get(false).size());

        System.out.println("\n4. Set Operations:");
        Set<String> uniqueTitles = library.getAllUniqueTitles();
        System.out.println("Unique titles in library: " + uniqueTitles.size());
        System.out.println("Sample titles:");
        uniqueTitles.stream().limit(3).forEach(title -> System.out.println("  - " + title));

        System.out.println("\n5. Map Operations:");
        Map<String, LibraryItem> itemMap = library.createItemMapById();
        System.out.println("Items in map: " + itemMap.size());
        itemMap.forEach((id, item) ->
                System.out.println("  " + id + " -> " + item.getTitle()));

        System.out.println("\n6. Wildcard Usage:");
        List<Book> booksList = library.getItemsByType(Book.class);
        System.out.println("Printing all books (using wildcard):");
        library.printAllItems(booksList);

        System.out.println("\n7. Type-Safe ItemCatalog:");
        ItemCatalog catalog = new ItemCatalog();
        library.getAllItems().forEach(catalog::addItem);

        Map<LibraryItemType, Integer> typeCounts = catalog.getTypeCounts();
        System.out.println("Item counts by type:");
        typeCounts.forEach((type, count) ->
                System.out.println("  " + type.getDisplayName() + ": " + count));

        System.out.println("\n8. Builder Pattern with Validation:");
        try {
            Book invalidBook = new Book.Builder(null, "Invalid Book", "Author")
                    .build();
        } catch (NullPointerException e) {
            System.out.println("Caught expected NPE for null ISBN");
        }

        try {
            Book invalidYearBook = new Book.Builder("123", "Test", "Author")
                    .setPublicationYear(3000)
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception for invalid year");
        }

        System.out.println("\n=== CHAPTER 9 (COLLECTIONS & GENERICS) COMPLETED ===");
    }
}