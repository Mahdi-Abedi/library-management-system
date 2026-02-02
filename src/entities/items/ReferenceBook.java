package entities.items;

import enums.LibraryItemType;

public final class ReferenceBook extends LibraryItem {
    private String subject;
    private String edition;
    private boolean inReadingRoomOnly;

    public ReferenceBook(String id, String title, String subject) {
        super(generateId(LibraryItemType.AUDIO_BOOK, id), title);
        this.subject = subject;
        this.inReadingRoomOnly = true;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public boolean isInReadingRoomOnly() {
        return inReadingRoomOnly;
    }

    public void setInReadingRoomOnly(boolean inReadingRoomOnly) {
        this.inReadingRoomOnly = inReadingRoomOnly;
    }

    @Override
    public boolean getAvailable() {
        return true;
    }

    @Override
    public void setAvailable(boolean available) {
        //do nothing
    }

    @Override
    public LibraryItemType getItemType() {
        return LibraryItemType.REFERENCE_BOOK;
    }
}
