package main;

import entities.Library;
import entities.items.*;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;
import exceptions.BorrowException;
import interfaces.ReportGenerator;
import io.FileHandler;
import io.LibraryDataManager;
import io.SerializationHandler;
import jdbc.BorrowRecordDAO;
import jdbc.DatabaseManager;
import jdbc.ItemDAO;
import jdbc.MemberDAO;
import services.LibraryTaskExecutor;
import services.LocalizationService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - COMPLETE DEMO ===\n");

        // ==================== 1. CREATING LIBRARY AND ITEMS  ====================
        System.out.println("1. CREATING LIBRARY AND ITEMS");
        Library library = new Library();

        Book effectiveJava = new Book.Builder("978-0134685991", "Effective Java", "Joshua Bloch")
                .setPublicationYear(2018).setPageCount(416).build();

        Book cleanCode = new Book.Builder("978-0132350884", "Clean Code", "Robert Martin")
                .setPublicationYear(2008).setPageCount(464).build();

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

        System.out.println("  Items added: 5");
        System.out.println("  Member added: " + ali.getName());

        // ==================== 2. GENERICS & COLLECTIONS ====================
        System.out.println("\n2. GENERICS AND COLLECTIONS DEMO");
        List<Book> books = library.getItemsByType(Book.class);
        System.out.println("  Books (generics): " + books.size());

        Map<LibraryItemType, List<LibraryItem>> grouped = library.groupItemsByType();
        grouped.forEach((type, items) ->
                System.out.println("  " + type + ": " + items.size()));

        Map<Boolean, List<LibraryItem>> partitioned = library.partitionByAvailability();
        System.out.println("  Available: " + partitioned.get(true).size());
        System.out.println("  Borrowed: " + partitioned.get(false).size());

        // ==================== 3. LAMBDAS & STREAMS ====================
        System.out.println("\n3. LAMBDAS AND STREAMS DEMO");
        Predicate<LibraryItem> isAvailable = LibraryItem::getAvailable;
        List<LibraryItem> available = library.findItems(isAvailable);
        System.out.println("  Available items (Predicate): " + available.size());

        library.processItems(item ->
                System.out.println("  Processing: " + item.getTitle()));

        List<String> titles = library.transformItems(LibraryItem::getTitle);
        System.out.println("  Titles via method ref: " + titles);

        long bookCount = library.itemStream()
                .filter(i -> i.getItemType() == LibraryItemType.BOOK)
                .count();
        System.out.println("  Book count via stream: " + bookCount);

        // ==================== 4. CONCURRENCY DEMO ====================
        System.out.println("\n4. CONCURRENCY DEMO (CompletableFuture)");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        LibraryTaskExecutor taskExecutor = new LibraryTaskExecutor(library);

        CompletableFuture<List<LibraryItem>> future = taskExecutor.processItemsAsync(library.getAllItems());
        future.thenAccept(items ->
                System.out.println("  Async processed " + items.size() + " items")
        ).join();

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ==================== 5. EXCEPTION HANDLING ====================
        System.out.println("\n5. EXCEPTION HANDLING");
        try {
            library.borrowItemWithException(javaSpec.getId(), ali);
        } catch (BorrowException e) {
            System.out.println("  Expected exception: " + e.getMessage());
        }

        // ==================== 6. LOCALIZATION ====================
        System.out.println("\n6. LOCALIZATION DEMO");
        LocalizationService english = new LocalizationService(Locale.ENGLISH);
        System.out.println("  English: " + english.getMessage("library.welcome"));

        LocalizationService persian = new LocalizationService(new Locale("fa", "IR"));
        System.out.println("  Persian: " + persian.getMessage("library.welcome"));

        // ==================== 7. I/O OPERATIONS ====================
        System.out.println("\n7. I/O OPERATIONS");
        try {
            FileHandler fileHandler = new FileHandler("library_data");
            for (LibraryItem item : library.getAllItems()) {
                fileHandler.saveItem(item);
            }
            System.out.println("  Saved items to files");

            List<Path> files = fileHandler.listAllItemFiles();
            System.out.println("  Found " + files.size() + " item files");

            SerializationHandler serialHandler = new SerializationHandler();
            serialHandler.serializeLibrary(library, "library.ser");
            System.out.println("  Library serialized");

            Library deserialized = serialHandler.deserializeLibrary("library.ser");
            System.out.println("  Library deserialized with " + deserialized.getAllItems().size() + " items");

            LibraryDataManager dataManager = new LibraryDataManager("library_data");
            dataManager.exportToCSV(library, "export.csv");
            System.out.println("  Exported to CSV");

            Files.deleteIfExists(Path.of("library.ser"));
            Files.deleteIfExists(Path.of("library_data/export.csv"));
            for (Path f : files) Files.deleteIfExists(f);
            System.out.println("  Cleaned up files");
        } catch (Exception e) {
            System.out.println("  I/O error (may be expected if directories missing): " + e.getMessage());
        }

        // ==================== 8. JDBC DATABASE ====================
        System.out.println("\n8. JDBC DATABASE DEMO");
        try (DatabaseManager dbManager = new DatabaseManager()) {
            dbManager.createTables();
            ItemDAO itemDAO = new ItemDAO(dbManager);
            MemberDAO memberDAO = new MemberDAO(dbManager);
            BorrowRecordDAO recordDAO = new BorrowRecordDAO(dbManager);

            // Insert
            for (LibraryItem item : library.getAllItems()) {
                itemDAO.insertItem(item);
            }
            memberDAO.insertMember(ali);
            System.out.println("  Inserted data into H2 in-memory DB");

            // Query
            List<LibraryItem> dbItems = itemDAO.findAll();
            System.out.println("  Items from DB: " + dbItems.size());

            // Borrow transaction
            var conn = dbManager.getConnection();
            conn.setAutoCommit(false);
            try {
                LibraryItem book = itemDAO.findById(effectiveJava.getId()).orElseThrow();
                Member member = memberDAO.findById(ali.getId()).orElseThrow();

                BorrowRecord record = new BorrowRecord();
                record.setItem(book);
                record.setMember(member);
                record.setBorrowDate(LocalDate.now());
                record.setDueDate(LocalDate.now().plusDays(14));

                recordDAO.insertRecord(record);
                itemDAO.updateAvailability(book.getId(), false);
                conn.commit();
                System.out.println("  Borrow transaction committed");
            } catch (Exception e) {
                conn.rollback();
                System.out.println("  Transaction rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

            // Return
            recordDAO.returnItem(effectiveJava.getId(), LocalDate.now());
            System.out.println("  Item returned");

            // Cleanup
            try (var stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM borrow_records");
            }
            for (LibraryItem item : library.getAllItems()) {
                itemDAO.deleteItem(item.getId());
            }
            memberDAO.deleteMember(ali.getId());
            System.out.println("  Cleaned up DB");
        } catch (Exception e) {
            System.out.println("  JDBC error (check H2 driver): " + e.getMessage());
        }

        // ==================== 9. SERVICE LOADER DEMO (if available) ====================
        System.out.println("\n9. SERVICE LOADER DEMO");
        try {
            java.util.ServiceLoader<ReportGenerator> loader = java.util.ServiceLoader.load(ReportGenerator.class);
            long count = loader.stream().count();
            System.out.println("  Found " + count + " ReportGenerator implementations");
        } catch (Exception e) {
            System.out.println("  ServiceLoader not used");
        }

        System.out.println("\n=== ALL CHAPTERS DEMO COMPLETED SUCCESSFULLY ===");
    }
}