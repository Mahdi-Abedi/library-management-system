package entites;

import java.util.List;

public record LibraryStatistics(
        long totalBooks,
        long availableBooks,
        long borrowedBooks,
        long totalMembers,
        long activeBorrowings,
        List<Book> recentBooks) {
}
