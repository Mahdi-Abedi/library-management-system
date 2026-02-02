package enums;

public enum LibraryItemType {
    BOOK("Book", 14, true, 500),
    MAGAZINE("Magazine", 7, true, 300),
    DVD("DVD", 21, true, 1000),
    AUDIO_BOOK("Audio Book", 3, true, 400),
    REFERENCE_BOOK("Reference Book", 3, false, 0),
    ;

    private final String displayName;
    private final int defaultLoanDays;
    private final boolean loanable;
    private final double dailyFine;

    LibraryItemType(String displayName, int defaultLoanDays, boolean loanable, double dailyFine) {
        this.displayName = displayName;
        this.defaultLoanDays = defaultLoanDays;
        this.loanable = loanable;
        this.dailyFine = dailyFine;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultLoanDays() {
        return defaultLoanDays;
    }

    public boolean isLoanable() {
        return loanable;
    }

    public double getDailyFine() {
        return dailyFine;
    }

    public String getLoanInfo() {
        if (!loanable) {
            return displayName + ": Not available for loan";
        }
        return String.format("%s: %d days loan, %.2f daily fine",
                displayName, defaultLoanDays, dailyFine);
    }
}
