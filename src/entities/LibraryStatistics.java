package entities;

import java.util.List;

public record LibraryStatistics(
        long totalItems,
        long availableItems,
        long borrowedItems,
        long totalMembers,
        long activeBorrowings,
        List<String> recentItemTitles) {

    public LibraryStatistics {
        if (totalItems < 0) throw new IllegalArgumentException("Total items cannot be negative");
        if (availableItems < 0) throw new IllegalArgumentException("Available items cannot be negative");
        if (borrowedItems < 0) throw new IllegalArgumentException("Borrowed items cannot be negative");
        if (totalMembers < 0) throw new IllegalArgumentException("Total members cannot be negative");

        if (availableItems + borrowedItems != totalItems) {
            throw new IllegalArgumentException(
                    String.format("Inconsistent counts: available(%d) + borrowed(%d) != total(%d)",
                            availableItems, borrowedItems, totalItems)
            );
        }

        recentItemTitles = List.copyOf(recentItemTitles);
    }

    public double getUtilizationPercentage() {
        return (totalItems == 0) ? 0 : (double) borrowedItems / totalItems * 100;
    }
}
