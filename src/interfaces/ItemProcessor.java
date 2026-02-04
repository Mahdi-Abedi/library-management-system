package interfaces;

import entities.items.LibraryItem;

@FunctionalInterface
public interface ItemProcessor {
    void process(LibraryItem item);
}
