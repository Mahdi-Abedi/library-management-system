package entities.items;

import enums.ItemStatus;
import enums.LibraryItemType;
import interfaces.LoanPolicy;

import java.io.Serializable;

public abstract sealed class LibraryItem implements Serializable permits Book, Magazine, DVD, ReferenceBook, AudioBook {
    private static final long serialVersionUID = 1L;

    protected boolean available;
    private final String id;
    private final String title;
    private ItemStatus status;

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
        this.available = true;
        this.status = ItemStatus.AVAILABLE;
    }

    public static String generateId(LibraryItemType itemType, String key) {
        return itemType.name() + "-" + key;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public abstract LibraryItemType getItemType();

    public boolean canBeBorrowed() {
        return available && this instanceof LoanPolicy;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LibraryItem item = (LibraryItem) obj;
        return id != null ? item.getId().equals(id) : item.getId() == null;
    }
}
