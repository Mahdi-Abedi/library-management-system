package main;

import entities.Library;
import entities.items.*;
import entities.people.Member;
import enums.MemberStatus;
import enums.MovieGenre;
import io.FileHandler;
import io.LibraryDataManager;
import io.SerializationHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 14 DEMO ===\n");

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

        try {
            System.out.println("1. FILE HANDLER DEMONSTRATION");
            FileHandler fileHandler = new FileHandler("library_data");

            for (LibraryItem item : library.getAllItems()) {
                fileHandler.saveItem(item);
                System.out.println("Saved: " + item.getId() + ".txt");
            }

            List<String> itemData = fileHandler.readItem(effectiveJava.getId());
            System.out.println("\nItem data for " + effectiveJava.getId() + ":");
            itemData.forEach(line -> System.out.println("  " + line));

            List<Path> itemFiles = fileHandler.listAllItemFiles();
            System.out.println("\nTotal item files: " + itemFiles.size());

            System.out.println("\n2. SERIALIZATION DEMONSTRATION");
            SerializationHandler serialHandler = new SerializationHandler();

            serialHandler.serializeLibrary(library, "library.ser");
            System.out.println("Library serialized to library.ser");

            Library deserializedLib = serialHandler.deserializeLibrary("library.ser");
            System.out.println("Library deserialized with " + deserializedLib.getAllItems().size() + " items");

            System.out.println("\n3. CSV EXPORT/IMPORT");
            LibraryDataManager dataManager = new LibraryDataManager("library_data");

            dataManager.exportToCSV(library, "library_export.csv");
            System.out.println("Library exported to library_export.csv");

            List<String[]> importedData = dataManager.importFromCSV("library_export.csv");
            System.out.println("Imported " + importedData.size() + " records from CSV");

            System.out.println("\n4. BACKUP CREATION");
            dataManager.createBackup();
            System.out.println("Backup created successfully");

            System.out.println("\n5. FILE SEARCH WITH PATTERNS");
            List<Path> foundFiles = dataManager.findFiles("*.txt");
            System.out.println("Found " + foundFiles.size() + " .txt files");

            System.out.println("\n6. FILE PROCESSING WITH LINES");
            dataManager.processFileLines("library_export.csv", line -> {
                if (!line.startsWith("ID")) {
                    System.out.println("CSV Record: " + line);
                }
            });

            System.out.println("\n7. NIO.2 FEATURES");
            Path testPath = Path.of("library_data/test_nio.txt");

            Files.writeString(testPath, "Test content with NIO.2");
            System.out.println("File written with NIO.2");

            String content = Files.readString(testPath);
            System.out.println("Read content: " + content);

            BasicFileAttributes attrs = Files.readAttributes(testPath, BasicFileAttributes.class);
            System.out.println("File size: " + attrs.size() + " bytes");
            System.out.println("Creation time: " + attrs.creationTime());

            Files.deleteIfExists(testPath);

            System.out.println("\n8. CLEANUP");
            for (LibraryItem item : library.getAllItems()) {
                fileHandler.deleteItemFile(item.getId());
            }
            Files.deleteIfExists(Path.of("library.ser"));
            Files.deleteIfExists(Path.of("library_export.csv"));
            System.out.println("Test files cleaned up");

        } catch (Exception e) {
            System.err.println("Error during I/O operations: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== CHAPTER 14 (I/O) COMPLETED ===");
    }
}