package main;

import entities.Library;
import entities.items.*;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.LibraryItemType;
import enums.MemberStatus;
import enums.MovieGenre;
import jdbc.BorrowRecordDAO;
import jdbc.DatabaseManager;
import jdbc.ItemDAO;
import jdbc.MemberDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM - CHAPTER 15 DEMO ===\n");

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

        try (DatabaseManager dbManager = new DatabaseManager()) {
            System.out.println("1. DATABASE CONNECTION");
            System.out.println("Connected to: " + dbManager.getConnection().getMetaData().getURL());

            System.out.println("\n2. CREATING TABLES");
            dbManager.createTables();
            System.out.println("Tables created successfully");

            System.out.println("\n3. INSERTING DATA WITH DAO");
            ItemDAO itemDAO = new ItemDAO(dbManager);
            MemberDAO memberDAO = new MemberDAO(dbManager);
            BorrowRecordDAO recordDAO = new BorrowRecordDAO(dbManager);

            for (LibraryItem item : library.getAllItems()) {
                itemDAO.insertItem(item);
                System.out.println("Inserted: " + item.getTitle());
            }

            memberDAO.insertMember(ali);
            System.out.println("Inserted member: " + ali.getName());

            System.out.println("\n4. QUERYING DATA");
            List<LibraryItem> allItems = itemDAO.findAll();
            System.out.println("All items in database: " + allItems.size());

            List<Member> allMembers = memberDAO.findAll();
            System.out.println("All members in database: " + allMembers.size());

            System.out.println("\n5. FINDING BY TYPE");
            List<LibraryItem> books = itemDAO.findByType(LibraryItemType.BOOK);
            System.out.println("Books found: " + books.size());

            System.out.println("\n6. COUNT BY TYPE");
            int bookCount = itemDAO.countByType(LibraryItemType.BOOK);
            int dvdCount = itemDAO.countByType(LibraryItemType.DVD);
            System.out.println("Books: " + bookCount);
            System.out.println("DVDs: " + dvdCount);

            System.out.println("\n7. BORROW OPERATION WITH TRANSACTION");
            Connection conn = dbManager.getConnection();
            try {
                conn.setAutoCommit(false);

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
                System.out.println("Borrow transaction committed successfully");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

            System.out.println("\n8. FINDING ACTIVE BORROW RECORDS");
            List<BorrowRecord> activeRecords = recordDAO.findActiveRecords();
            System.out.println("Active borrows: " + activeRecords.size());

            System.out.println("\n9. RETURNING ITEM");
            boolean returned = recordDAO.returnItem(effectiveJava.getId(), LocalDate.now());
            System.out.println("Item returned: " + returned);

            System.out.println("\n10. PREPARED STATEMENT EXAMPLE");
            String searchSql = "SELECT * FROM items WHERE title LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(searchSql)) {
                pstmt.setString(1, "%Java%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("Items with 'Java' in title:");
                    while (rs.next()) {
                        System.out.println("  - " + rs.getString("title"));
                    }
                }
            }

            System.out.println("\n11. METADATA INFORMATION");
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName());

            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("\nTables in database:");
            while (tables.next()) {
                System.out.println("  - " + tables.getString("TABLE_NAME"));
            }

            System.out.println("\n12. CLEANUP - DELETING TEST DATA");
            try (Statement stmt = dbManager.getConnection().createStatement()) {
                int deletedRecords = stmt.executeUpdate("DELETE FROM borrow_records");
                System.out.println("Deleted " + deletedRecords + " borrow records");
            } catch (SQLException e) {
                System.out.println("Error deleting borrow records: " + e.getMessage());
            }

            for (LibraryItem item : library.getAllItems()) {
                itemDAO.deleteItem(item.getId());
            }
            memberDAO.deleteMember(ali.getId());
            System.out.println("Test data cleaned up");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=== CHAPTER 15 (JDBC) COMPLETED ===");
    }
}