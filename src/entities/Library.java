package entities;

import entities.items.Book;
import entities.items.DVD;
import entities.items.LibraryItem;
import entities.items.Magazine;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.LibraryItemType;
import exceptions.BorrowException;
import exceptions.ItemNotFoundException;
import interfaces.LoanPolicy;
import services.BorrowingService;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Library implements Serializable {
    private static final long serialVersionUID = 1L;

    private final transient BorrowingService borrowingService;

    private final static int MAX_REPORT_ITEMS = 5;

    private final List<LibraryItem> items;
    private final List<Member> members;
    private final List<BorrowRecord> borrowRecords;

    private final AtomicInteger totalBorrowOperations = new AtomicInteger(0);
    private final ConcurrentHashMap<LibraryItemType, AtomicInteger> borrowCountByType;

    public Library() {
        borrowingService = new BorrowingService();
        this.items = new CopyOnWriteArrayList<>();
        this.members = new CopyOnWriteArrayList<>();
        this.borrowRecords = new CopyOnWriteArrayList<>();

        this.borrowCountByType = new ConcurrentHashMap<>();
        for (LibraryItemType type : LibraryItemType.values()) {
            borrowCountByType.put(type, new AtomicInteger(0));
        }
    }

    public BorrowingService getBorrowingService() {
        return borrowingService;
    }

    public boolean addItem(LibraryItem item) {
        if (item == null || item.getId() == null)
            return false;

        synchronized (items) {
            boolean exists = items.stream().anyMatch(b -> b.equals(item));
            if (exists)
                return false;

            return this.items.add(item);
        }
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

    public List<LibraryItem> findItems(Predicate<LibraryItem> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
        return items.stream()
                .filter(item -> item != null && predicate.test(item))
                .toList();
    }

    public List<LibraryItem> searchItemsWithMethodRef(String keyword) {
        // Check if keyword is null/empty after trimming or items collection is null
        if (keyword == null || keyword.trim().isEmpty() || items == null) {
            return Collections.emptyList();
        }

        // Create trimmed keyword once to avoid repeated trimming in containsKeyword method
        String trimmedKeyword = keyword.trim();

        // Enhanced validation to prevent potential security issues
        if (!isValidSearchKeyword(trimmedKeyword) || hasDangerousPatterns(trimmedKeyword)) {
            return Collections.emptyList();
        }

        try {
            return items.stream()
                    .filter(Objects::nonNull)  // Filter out null items first
                    .filter(item -> safeContainsKeyword(item, trimmedKeyword))  // Apply keyword filter with safe exception handling
                    .collect(Collectors.toUnmodifiableList());  // Collect to unmodifiable list
        } catch (UnsupportedOperationException | NullPointerException e) {
            // Log the specific error for debugging
            System.err.println("Error during stream processing: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private boolean isValidSearchKeyword(String keyword) {
        if (keyword == null) {
            return false;
        }

        // Add length validation to prevent extremely long searches
        if (keyword.length() > 100) {
            return false;
        }

        // Basic alphanumeric and space validation
        return keyword.matches("^[a-zA-Z0-9\\s\\-_.,!?]*$");
    }

    private boolean hasDangerousPatterns(String keyword) {
        if (keyword == null) {
            return false;
        }

        // Check for common dangerous patterns
        String[] dangerousPatterns = {
                "<script", "javascript:", "vbscript:", "onerror=", "onclick=",
                "eval\\(", "expression\\(", "<iframe", "<object", "<embed",
                "\\b(union|select|insert|update|delete|drop|create|alter|exec|execute)\\b"
        };

        String lowerKeyword = keyword.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerKeyword.contains(pattern.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private boolean safeContainsKeyword(LibraryItem item, String keyword) {
        try {
            return containsKeyword(item, keyword);
        } catch (NullPointerException e) {
            // Log the error with context but continue processing other items
            System.err.println("Null pointer encountered when checking keyword for item: " +
                    (item != null ? item.getTitle() : "null item"));
            return false;
        } catch (Exception e) {
            // Log the error with context but continue processing other items
            System.err.println("Unexpected error when checking keyword for item: " + e.getMessage());
            return false;
        }
    }


    private boolean containsKeyword(LibraryItem item, String keyword) {
        return item.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }

    public List<LibraryItem> getItemsSortedBy(Comparator<LibraryItem> comparator) {
        return items.stream().sorted(comparator).toList();
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

    public <T extends LibraryItem> List<T> getItemsByType(Class<T> type) {
        return items.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public <T extends LibraryItem> Optional<T> findItemByTypeAndId(Class<T> type, String id) {
        return items.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public Map<LibraryItemType, List<LibraryItem>> groupItemsByType() {
        return items.stream().collect(Collectors.groupingBy(LibraryItem::getItemType));
    }

    public Map<Boolean, List<LibraryItem>> partitionByAvailability() {
        return items.stream().collect(Collectors.partitioningBy(LibraryItem::getAvailable));
    }

    public Set<String> getAllUniqueTitles() {
        return items.stream().map(LibraryItem::getTitle).collect(Collectors.toSet());
    }

    public Map<String, LibraryItem> createItemMapById() {
        return items.stream().collect(Collectors.toMap(LibraryItem::getId, Function.identity(), (existing, replacement) -> existing));
    }

    public void printAllItems(List<? extends LibraryItem> items) {
        items.forEach(item ->
                System.out.println(item.getTitle() + " - " + item.getItemType()));
    }

    public void addMultipleItemsFromCollection(Collection<? extends LibraryItem> newItems) {
        newItems.forEach(this::addItem);
    }

    public List<? super LibraryItem> getItemsAsSuperList() {
        return new ArrayList<Object>(items);
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

    public BorrowingService.BorrowResult borrowItem(String itemId, Member member) {
        return borrowItem(itemId, member, null);
    }

    public BorrowingService.BorrowResult borrowItem(String itemId) {
        if (members.isEmpty())
            throw new IllegalStateException("No members available");

        return borrowItem(itemId, members.getFirst());
    }

    public BorrowingService.BorrowResult borrowItem(LibraryItem item, Member member) {
        return borrowItem(item.getId(), member);

    }

    public BorrowingService.BorrowResult borrowItem(String itemId, Member member, Integer customDays) {
        Optional<LibraryItem> item = findItemById(itemId);
        if (item.isEmpty())
            return BorrowingService.BorrowResult.failure("Item not found");

        synchronized (item.get()) {
            BorrowingService.BorrowResult result = borrowingService.borrowItem(item.get(), member, customDays);
            if (result.isSuccess())
                borrowRecords.add(result.getRecord());

            return result;
        }
    }

    public int borrowMultipleItems(Member member, String... ids) {
        if (member == null || ids == null || ids.length == 0)
            return 0;
        int total = 0;
        for (String id : ids) {
            BorrowingService.BorrowResult result = borrowItem(id, member);
            if (result.isSuccess())
                total++;
        }

        return total;
    }

    public int borrowMultipleItems(Member member, LibraryItem... items) {
        if (member == null || items == null || items.length == 0)
            return 0;
        int total = 0;
        for (LibraryItem item : items) {
            BorrowingService.BorrowResult result = borrowItem(item, member);
            if (result.isSuccess())
                total++;
        }

        return total;
    }

    public boolean returnItem(String id) {
        if (id == null || id.isBlank())
            return false;

        return findActiveBorrowRecord(id)
                .map(borrowRecord -> {
                    borrowingService.returnItem(borrowRecord.getItem());
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

    public void processItems(Consumer<LibraryItem> consumer) {
        items.forEach(consumer);
    }

    public <R> List<R> transformItems(Function<LibraryItem, R> function) {
        return items.stream().map(function).toList();
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

    public Stream<LibraryItem> itemStream() {
        return items.stream();
    }

    public List<LibraryItem> findAvailableItemsSortedByTitle() {
        return itemStream()
                .filter(LibraryItem::getAvailable)
                .sorted(Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<String> getAllTitlesUppercase() {
        return itemStream()
                .map(LibraryItem::getTitle)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    public Optional<LibraryItem> findFirstAvailableBook() {
        return itemStream()
                .filter(item -> item.getItemType() == LibraryItemType.BOOK)
                .filter(LibraryItem::getAvailable)
                .findFirst();
    }

    public boolean hasItemWithTitle(String title) {
        return itemStream()
                .anyMatch(item -> item.getTitle().equalsIgnoreCase(title));
    }

    public long countItemsByCondition(Predicate<LibraryItem> condition) {
        return itemStream()
                .filter(condition)
                .count();
    }

    public Map<LibraryItemType, String> getTypeToTitlesMap() {
        return itemStream()
                .collect(Collectors.groupingBy(
                        LibraryItem::getItemType,
                        Collectors.mapping(
                                LibraryItem::getTitle,
                                Collectors.joining(", ")
                        )
                ));
    }

    public Optional<LibraryItem> findMostRecentItem() {
        return itemStream()
                .max(Comparator.comparing(item -> {
                    if (item instanceof Book book) {
                        return book.getPublicationYear();
                    } else if (item instanceof Magazine magazine) {
                        return magazine.getPublicationDate().getYear();
                    }
                    return 0;
                }));
    }

    public DoubleSummaryStatistics getBookPageStatistics() {
        return itemStream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .mapToDouble(Book::getPageCount)
                .summaryStatistics();
    }

    public List<LibraryItem> getDistinctItemsByType() {
        return itemStream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(LibraryItem::getItemType))),
                        ArrayList::new
                ));
    }

    public List<LibraryItem> processItemsInParallel(Consumer<LibraryItem> action) {
        return items.parallelStream().peek(action).toList();
    }

    public Map<LibraryItemType, Long> countItemsByTypeParallel() {
        return items.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        LibraryItem::getItemType,
                        Collectors.counting()));
    }

    public Map<LibraryItemType, Double> getAverageValuesByType() {
        return itemStream()
                .collect(Collectors.groupingBy(
                        LibraryItem::getItemType,
                        Collectors.averagingDouble(this::calculateTotalValue)
                ));
    }

    public Map<Boolean, List<String>> partitionTitlesByLoanability() {
        return itemStream()
                .collect(Collectors.partitioningBy(
                        LibraryItem::canBeBorrowed,
                        Collectors.mapping(
                                LibraryItem::getTitle,
                                Collectors.toList()
                        )
                ));
    }

    public String getAllTitlesAsSingleString() {
        return itemStream()
                .map(LibraryItem::getTitle)
                .collect(Collectors.joining("; ", "[", "]"));
    }

    public void borrowItemWithException(String itemId, Member member) throws BorrowException {
        LibraryItem item = getItemByIdOrThrow(itemId);

        getBorrowingService().borrowItemWithException(item, member);
    }

    public Statistics getStats() {
        return new Statistics();
    }

    public class Statistics {
        public long getTotalItems() {
            return items.size();
        }

        public long getAvailableItems() {
            return items.stream()
                    .filter(LibraryItem::getAvailable)
                    .count();
        }

        public long getLoanableItems() {
            return items.stream()
                    .filter(LibraryItem::canBeBorrowed)
                    .count();
        }

        public Map<LibraryItemType, Long> getCountByType() {
            return items.stream()
                    .collect(Collectors.groupingBy(
                            LibraryItem::getItemType,
                            Collectors.counting()
                    ));
        }

        public LibraryStatistics generateStatistics() {
            List<String> recentTitles = items.stream()
                    .limit(5)
                    .map(LibraryItem::getTitle)
                    .collect(Collectors.toList());

            return new LibraryStatistics(
                    getTotalItems(),
                    getAvailableItems(),
                    getTotalItems() - getAvailableItems(),
                    members.size(),
                    borrowRecords.stream()
                            .filter(r -> r.getReturnDate() == null)
                            .count(),
                    recentTitles
            );
        }
    }
}
