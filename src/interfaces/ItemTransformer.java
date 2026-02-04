package interfaces;

import entities.items.LibraryItem;

@FunctionalInterface
public interface ItemTransformer<T> {
    T transform(LibraryItem item);
}
