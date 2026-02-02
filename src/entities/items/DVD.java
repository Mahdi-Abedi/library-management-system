package entities.items;

import enums.LibraryItemType;
import enums.MovieGenre;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

public final class DVD extends LibraryItem implements LoanPolicy, ReservationPolicy {
    private String director;
    private int durationMinutes;
    private MovieGenre genre;
    private int releaseYear;
    private String studio;
    private double imdbRating;

    public DVD(String id, String title, String director) {
        super(id, title);
        this.director = director;
        this.genre = MovieGenre.EDUCATIONAL;
    }

    public DVD(String id, String title, String director, MovieGenre genre, int duration) {
        super(id, title);
        this.director = director;
        this.genre = genre;
        this.durationMinutes = duration;
    }

    @Override
    public LibraryItemType getItemType() {
        return LibraryItemType.DVD;
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

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getDurationFormatted() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return String.format("%d hr %d min", hours, minutes);
    }

    public boolean isFamilyFriendly() {
        return genre != null && genre.isFamilyFriendly();
    }

    public boolean isAgeAppropriateFor(int age) {
        return genre != null && genre.isAgeAppropriate(age);
    }

    @Override
    public int getMaxLoanDays() {
        if (genre == MovieGenre.EDUCATIONAL || genre == MovieGenre.DOCUMENTARY) {
            return 28;
        }
        return 21;
    }

    @Override
    public double getDailyFine() {
        if (genre == MovieGenre.EDUCATIONAL) {
            return 2000.0;
        }
        return 1000.0;
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
        return 7;
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
                .append("\n\tGenre: ").append(genre != null ? genre.getDisplayName() : "Not specified")
                .append("\n\tDuration: ").append(getDurationFormatted())
                .append("\n\tRelease Year: ").append(releaseYear > 0 ? releaseYear : "Unknown")
                .append("\n\tAge Rating: ").append(genre != null ? genre.getAgeRating() : "Not rated")
                .append("\n\tAvailable: ").append(getAvailable())
                .append("\n\tFamily Friendly: ").append(isFamilyFriendly())
                .toString();
    }
}