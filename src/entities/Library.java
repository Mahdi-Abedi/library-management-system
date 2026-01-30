package entities;

import entities.items.Book;
import entities.items.DVD;
import entities.items.LibraryItem;
import entities.items.Magazine;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.LibraryItemType;
import exceptions.ItemNotFoundException;
import interfaces.LoanPolicy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {
    private final static int MAX_REPORT_ITEMS = 5;

    private final List<LibraryItem> items;
    private final List<Member> members;
    private final List<BorrowRecord> borrowRecords;

    public Library() {
        this.items = new ArrayList<>();
        this.members = new ArrayList<>();
        this.borrowRecords = new ArrayList<>();
    }

    public boolean addItem(LibraryItem item) {
        if (item == null || item.getId() == null)
            return false;

        boolean exists = items.stream().anyMatch(b -> b.equals(item));
        if (exists)
            return false;

        return this.items.add(item);
    }

    public boolean addMember(Member member) {
        if (member == null)
            return false;

        return members.add(member);
    }

    public int addMultipleItems(LibraryItem... items) {
        if (items == null || items.length == 0)
            return 0;

        int total = 0;
        for (LibraryItem item : items) {
            if (addItem(item))
                total++;
        }
        return total;
    }

    public boolean removeItem(String id) {
        return this.items.removeIf(b -> b.getId().equals(id));
    }

    public Book searchBook(String title, String author) {
        if (items == null || items.isEmpty())
            return null;

        return getItemsByType(LibraryItemType.BOOK).stream()
                .map(item -> (Book) item)
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .findFirst().orElse(null);
    }

    public List<LibraryItem> searchItems(String keyword) {
        if (items == null || items.isEmpty())
            return null;
        return items.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

    }

    public List<Book> findAllBookByAuthor(String author) {
        if (items == null || items.isEmpty() || author == null || author.isBlank())
            return null;
        return getItemsByType(LibraryItemType.BOOK).stream()
                .map(item -> (Book) item)
                .filter(book -> book.getAuthor().equals(author))
                .toList();
    }

    public Optional<LibraryItem> findItemById(String id) {
        if (items == null || items.isEmpty() || id == null || id.isBlank())
            return Optional.empty();

        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();

    }

    public Optional<BorrowRecord> findActiveBorrowRecord(String id) {
        if (borrowRecords == null || borrowRecords.isEmpty() || id == null || id.isBlank())
            return Optional.empty();

        return borrowRecords.stream()
                .filter(borrowRecord -> borrowRecord.getItem().getId().equals(id) &&
                        borrowRecord.getReturnDate() == null)
                .findFirst();
    }

    public List<LibraryItem> getAvailableItems() {
        return items.stream()
                .filter(LibraryItem::getAvailable)
                .toList();

    }

    public List<LibraryItem> getItemsByType(LibraryItemType type) {
        return items.stream()
                .filter(item -> item.getItemType() == type)
                .collect(Collectors.toList());
    }

    public String generateLibraryReport() {
        long itemCount = items.size();
        long availableCount = getAvailableItems().size();

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(50)).append("\n");
        report.append("LIBRARY REPORT\n");
        report.append("=".repeat(50)).append("\n\n");

        report.append(String.format("Total Items: %d%n", itemCount));
        report.append(String.format("Available: %d%n", availableCount));
        report.append(String.format("Borrowed: %d%n", itemCount - availableCount));

        report.append("\nLatest " + Math.min(MAX_REPORT_ITEMS, items.size()) + " items:\n");
        items.stream().limit(MAX_REPORT_ITEMS)
                .forEach(item -> {
                    report.append("  • ").append(item.getTitle())
                            .append(" (").append(item.getItemType()).append(")\n");
                });
        List<BorrowRecord> overdue = borrowRecords.stream()
                .filter(BorrowRecord::isOverdue)
                .toList();

        if (!overdue.isEmpty()) {
            report.append("OVERDUE ITEMS:\n");
            report.append("-".repeat(40)).append("\n");
            overdue.forEach(record ->
                    report.append(String.format("  • %s - Due: %s (Overdue: %d days)\n",
                            record.getItem().getTitle(),
                            record.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                            record.getDaysOverdue()))
            );
            report.append("\n");
        }

        return report.toString();
    }

    public double calculateTotalValue(LibraryItem item) {
        if (item instanceof Book book) {
            return book.getPageCount() * 100;
        } else if (item instanceof Magazine magazine) {
            return 5000;
        } else if (item instanceof DVD dvd) {
            return 15000;
        }

        return Double.NaN;
    }

    public List<Book> searchBooks(String keyword) {
        if (items == null || items.isEmpty() || keyword == null || keyword.isBlank())
            return null;

        return getItemsByType(LibraryItemType.BOOK).stream()
                .map(item -> (Book) item)
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getIsbn().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public LibraryItem getItemByIdOrThrow(String id) {
        return findItemById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item with ID " + id + " not found"));
    }

    public LibraryItem[] getItemsAsArray() {
        return items.toArray(new LibraryItem[0]);
    }

    public BorrowRecord borrowItem(String id, Member member) {
        if (id == null || member == null || id.isBlank())
            return null;

        Optional<LibraryItem> itemOptional = findItemById(id);
        if (itemOptional.isEmpty() || !itemOptional.get().getAvailable())
            return null;

        BorrowRecord borrowRecord = new BorrowRecord(itemOptional.get(), member);
        borrowRecords.add(borrowRecord);

        return borrowRecord;

    }

    public BorrowRecord borrowItem(String isbn) {
        if (members.isEmpty())
            throw new IllegalStateException("No members available");

        return borrowItem(isbn, members.getFirst());
    }

    public BorrowRecord borrowItem(LibraryItem item, Member member) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null");
        return borrowItem(item.getId(), member);

    }

    public BorrowRecord borrowItem(String id, Member member, int days) {
        if (id == null || id.isBlank() || member == null)
            return null;

        if (days <= 0)
            throw new IllegalArgumentException("Borrow days must be positive");

        if (days > 30)
            throw new IllegalArgumentException("Maximum borrow period is 30 days");


        Optional<LibraryItem> itemOptional = findItemById(id);
        if (itemOptional.isEmpty() || !itemOptional.get().getAvailable())
            return null;

        BorrowRecord borrowRecord = new BorrowRecord(itemOptional.get(), member);
        borrowRecord.setDueDate(LocalDate.now().plusDays(days));
        borrowRecords.add(borrowRecord);

        return borrowRecord;

    }

    public int borrowMultipleItems(Member member, String... ids) {
        if (member == null || ids == null || ids.length == 0)
            return 0;
        int total = 0;
        for (String id : ids) {
            BorrowRecord borrowRecord = borrowItem(id, member);
            if (borrowRecord != null)
                total++;
        }

        return total;
    }

    public int borrowMultipleItems(Member member, LibraryItem... items) {
        if (member == null || items == null || items.length == 0)
            return 0;
        int total = 0;
        for (LibraryItem item : items) {
            BorrowRecord borrowRecord = borrowItem(item, member);
            if (borrowRecord != null)
                total++;
        }

        return total;
    }

    public boolean returnItem(String id) {
        if (id == null || id.isBlank())
            return false;

        return findActiveBorrowRecord(id)
                .map(borrowRecord -> {
                    borrowRecord.returnItem();
                    return true;
                })
                .orElse(false);
    }

    public int returnMultipleItems(String... isbns) {
        if (isbns == null || isbns.length == 0)
            return 0;

        int total = 0;
        for (String isbn : isbns) {
            if (returnItem(isbn))
                total++;
        }

        return total;
    }

    public List<LibraryItem> getAllItems() {
        return new ArrayList<>(items);
    }

    public List<LibraryItem> getLoanableItems() {
        return items.stream()
                .filter(LibraryItem::getAvailable)
                .filter(item -> item instanceof LoanPolicy)
                .toList();
    }

    public long countItemsByType(LibraryItemType type) {
        return items.stream().filter(item -> item.getItemType() == type).count();
    }
}
