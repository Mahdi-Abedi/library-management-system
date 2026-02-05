package services;

import entities.items.LibraryItem;
import enums.LibraryItemType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ItemCatalog {

    private final Map<LibraryItemType, List<LibraryItem>> catalog;


    public ItemCatalog() {
        this.catalog = new EnumMap<>(LibraryItemType.class);
        for (LibraryItemType itemType : LibraryItemType.values())
            catalog.put(itemType, new ArrayList<>());
    }

    public <T extends LibraryItem> void addItem(T item) {
        catalog.get(item.getItemType()).add(item);
    }

    public <T extends LibraryItem> List<T> getItemsOfType(Class<T> type,
                                                          LibraryItemType itemType) {
        return catalog.get(itemType).stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public Map<LibraryItemType, Integer> getTypeCounts() {
        Map<LibraryItemType, Integer> counts = new EnumMap<>(LibraryItemType.class);
        catalog.forEach((type, items) -> counts.put(type, items.size()));
        return counts;
    }
}
