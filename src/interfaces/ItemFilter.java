package interfaces;

import entities.items.LibraryItem;

@FunctionalInterface
public interface ItemFilter {

    boolean test(LibraryItem item);
}
