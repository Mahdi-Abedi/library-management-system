package entities.items;

import enums.LibraryItemType;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

public final class AudioBook extends LibraryItem implements LoanPolicy, ReservationPolicy {

    private String narrator;
    private int durationMinutes;

    public AudioBook(String id, String title) {
        super(generateId(LibraryItemType.AUDIO_BOOK, id), title);
    }

    public String narrator() {
        return narrator;
    }

    public AudioBook setNarrator(String narrator) {
        this.narrator = narrator;
        return this;
    }

    public int durationMinutes() {
        return durationMinutes;
    }

    public AudioBook setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        return this;
    }

    @Override
    public int getMaxLoanDays() {
        return 21;
    }

    @Override
    public double getDailyFine() {
        return 400;
    }

    @Override
    public boolean isRenewable() {
        return true;
    }

    @Override
    public int getMaxRenewals() {
        return 2;
    }

    @Override
    public int getMaxReservationDays() {
        return 5;
    }

    @Override
    public int getMaxSimultaneousReservations() {
        return 2;
    }

    @Override
    public LibraryItemType getItemType() {
        return LibraryItemType.AUDIO_BOOK;
    }
}
