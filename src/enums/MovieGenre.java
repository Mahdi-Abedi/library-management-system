package enums;

import java.util.Optional;

public enum MovieGenre {
    ACTION("Action", 18),
    COMEDY("Comedy", 12),
    DRAMA("Drama", 15),
    SCIENCE_FICTION("Science Fiction", 12),
    HORROR("Horror", 18),
    DOCUMENTARY("Documentary", 0),
    EDUCATIONAL("Educational", 0),
    ANIMATION("Animation", 6),
    FANTASY("Fantasy", 12),
    MYSTERY("Mystery", 15),
    ROMANCE("Romance", 15),
    THRILLER("Thriller", 18),
    ADVENTURE("Adventure", 12),
    BIOGRAPHY("Biography", 12),
    MUSICAL("Musical", 6),
    WAR("War", 18),
    WESTERN("Western", 15),
    CRIME("Crime", 18),
    FAMILY("Family", 6),
    HISTORY("History", 12);

    private final String displayName;
    private final int minimumAge;

    MovieGenre(String displayName, int minimumAge) {
        this.displayName = displayName;
        this.minimumAge = minimumAge;
    }

    public static Optional<MovieGenre> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String searchName = name.trim().toLowerCase();
        for (MovieGenre genre : values()) {
            if (genre.displayName.toLowerCase().equals(searchName) ||
                    genre.name().toLowerCase().equals(searchName)) {
                return Optional.of(genre);
            }
        }
        return Optional.empty();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public boolean isAgeAppropriate(int age) {
        return age >= minimumAge;
    }

    public boolean isFamilyFriendly() {
        return minimumAge <= 12;
    }

    public boolean requiresAdultSupervision() {
        return minimumAge > 12 && minimumAge < 18;
    }

    public boolean isForAdultsOnly() {
        return minimumAge >= 18;
    }

    public String getAgeRating() {
        if (minimumAge == 0) return "All Ages";
        if (minimumAge < 13) return "PG";
        if (minimumAge < 18) return "PG-13";
        return "R (18+)";
    }

    public Category getCategory() {
        if (minimumAge < 13) return Category.GENERAL_AUDIENCE;
        if (minimumAge < 18) return Category.PARENTAL_GUIDANCE;
        return Category.ADULTS_ONLY;
    }

    @Override
    public String toString() {
        return String.format("%s - %s",
                displayName, getAgeRating());
    }

    public enum Category {
        GENERAL_AUDIENCE,
        PARENTAL_GUIDANCE,
        ADULTS_ONLY
    }
}