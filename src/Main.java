import entities.Library;
import entities.items.*;
import entities.people.Member;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;
import exceptions.BorrowException;
import exceptions.ItemNotAvailableException;
import services.LibraryExporter;
import services.LocalizationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 11 DEMO ===\n");

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

        System.out.println("1. CUSTOM EXCEPTION HANDLING");
        System.out.println("Testing regular book borrow:");
        try {
            library.borrowItemWithException(effectiveJava.getId(), ali);
            System.out.println("✓ Borrow successful for regular book");
        } catch (BorrowException e) {
            System.out.println("✗ Unexpected exception: " + e.getMessage());
        }

        System.out.println("\nTesting reference book (should fail):");
        try {
            library.borrowItemWithException(javaSpec.getId(), ali);
            System.out.println("✗ Should not reach here for reference book");
        } catch (BorrowException e) {
            System.out.println("✓ Caught expected: " + e.getClass().getSimpleName());
        }

        System.out.println("\nTesting already borrowed book:");
        try {
            library.borrowItemWithException(effectiveJava.getId(), ali);
            System.out.println("✗ Should not reach here");
        } catch (ItemNotAvailableException e) {
            System.out.println("✓ Caught ItemNotAvailableException");
        } catch (BorrowException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
        }

        System.out.println("\n2. TRY-WITH-RESOURCES EXAMPLE");
        LibraryExporter exporter = new LibraryExporter();
        String tempFile = "library_export_" + System.currentTimeMillis() + ".txt";

        try {
            exporter.exportToFile(library, tempFile);
            System.out.println("✓ Export completed to: " + tempFile);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String firstLine = reader.readLine();
                System.out.println("First line: " + firstLine);
            }

        } catch (IOException e) {
            System.out.println("✗ Export failed: " + e.getMessage());
        }

        System.out.println("\n3. LOCALIZATION DEMO");
        LocalizationService englishService = new LocalizationService(Locale.ENGLISH);
        System.out.println("English: " + englishService.getMessage("library.welcome"));
        System.out.println("Date: " + englishService.formatDate(LocalDate.now()));
        System.out.println("Number: " + englishService.formatNumber(1234.567));

        LocalizationService persianService = new LocalizationService(
                new Locale("fa", "IR"));
        System.out.println("\nPersian: " + persianService.getMessage("library.welcome"));

        System.out.println("\n4. MULTI-CATCH EXAMPLE");
        try {
            library.borrowItem("NON_EXISTENT", ali);
            library.borrowItem(effectiveJava.getId(), null);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("✓ Caught in multi-catch: " + e.getClass().getSimpleName());
        }

        System.out.println("\n5. FINALLY BLOCK DEMO");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("non_existent_file.txt"));
            String line = reader.readLine();
        } catch (IOException e) {
            System.out.println("✓ File error: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    System.out.println("✓ Reader closed in finally");
                } catch (IOException e) {
                    System.out.println("Error closing: " + e.getMessage());
                }
            }
        }

        System.out.println("\n6. SUPPRESSED EXCEPTIONS");
        class TestResource implements AutoCloseable {
            private final String name;
            public TestResource(String name) { this.name = name; }
            @Override
            public void close() throws Exception {
                throw new IllegalStateException("Error closing " + name);
            }
        }

        try (TestResource r1 = new TestResource("R1");
             TestResource r2 = new TestResource("R2")) {
            throw new IOException("Primary error");
        } catch (Exception e) {
            System.out.println("Primary: " + e.getMessage());
            System.out.println("Suppressed: " + e.getSuppressed().length);
        }

        System.out.println("\n7. STREAM WITH EXCEPTION HANDLING");
        try {
            Map<LibraryItemType, List<LibraryItem>> itemsByType = library.groupItemsByType();
            System.out.println("Grouped " + itemsByType.size() + " item types");
        } catch (Exception e) {
            System.out.println("Stream error: " + e.getMessage());
        }

        System.out.println("\n8. RETURN ITEM FOR CLEANUP");
        boolean returned = library.returnItem(effectiveJava.getId());
        System.out.println("Item returned: " + returned);

        System.out.println("\n=== CHAPTER 11 COMPLETED ===");
    }
}