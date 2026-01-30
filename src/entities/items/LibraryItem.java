package entities.items;

import enums.LibraryItemType;

public abstract class LibraryItem {

    protected boolean available;
    private String id;
    private String title;

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
        this.available = true;
    }

    public static String generateId(LibraryItemType itemType, String key) {
        return itemType.name() + "-" + key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public abstract LibraryItemType getItemType();

    public void returnItem() {
        available = true;
    }

    public void borrowItem() {
        available = false;
    }
}
