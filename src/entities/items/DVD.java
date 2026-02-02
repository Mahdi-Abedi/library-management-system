package entities.items;

import enums.LibraryItemType;
import enums.MovieGenre;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

public final class DVD extends LibraryItem implements LoanPolicy, ReservationPolicy {
    private String director;
    private int durationMinutes;
    private MovieGenre genre;

    public DVD(String id, String title, String director) {
        super(generateId(LibraryItemType.DVD, id), title);
        this.director = director;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    @Override
    public int getMaxLoanDays() {
        return 21;
    }

    @Override
    public double getDailyFine() {
        return 1000;
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
        return 1;
    }

    @Override
    public String toString() {
        return new StringBuilder("DVD Details:")
                .append("\n\tTitle: ").append(getTitle())
                .append("\n\tDirector: ").append(director)
                .append("\n\tDuration: ").append(durationMinutes).append(" mins")
                .append("\n\tAvailable: ").append(getAvailable())
                .toString();
    }

    @Override
    public LibraryItemType getItemType() {
        return LibraryItemType.DVD;
    }
}
