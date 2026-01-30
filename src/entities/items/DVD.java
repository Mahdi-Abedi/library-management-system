package entities.items;

import enums.LibraryItemType;
import enums.MovieGenre;

public class DVD extends LibraryItem {
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
    public LibraryItemType getItemType() {
        return LibraryItemType.DVD;
    }
}
