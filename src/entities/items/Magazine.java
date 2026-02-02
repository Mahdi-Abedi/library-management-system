package entities.items;

import enums.LibraryItemType;
import interfaces.LoanPolicy;
import interfaces.ReservationPolicy;

import java.time.LocalDate;

public final class Magazine extends LibraryItem implements LoanPolicy, ReservationPolicy {
    private String issueNumber;
    private LocalDate publicationDate;
    private String publisher;

    public Magazine(String title, String issueNumber, LocalDate publicationDate) {
        super(generateId(LibraryItemType.MAGAZINE, issueNumber), title);
        this.issueNumber = issueNumber;
        this.publicationDate = publicationDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public int getMaxLoanDays() {
        return 7;
    }

    @Override
    public double getDailyFine() {
        return 300.0;
    }

    @Override
    public boolean isRenewable() {
        return false;
    }

    @Override
    public int getMaxRenewals() {
        return 0;
    }

    @Override
    public int getMaxReservationDays() {
        return 2;
    }

    @Override
    public int getMaxSimultaneousReservations() {
        return 1;
    }

    @Override
    public LibraryItemType getItemType() {
        return LibraryItemType.MAGAZINE;
    }

    @Override
    public String toString() {
        return new StringBuilder("Magazine Details:")
                .append("\n\tTitle: ").append(getTitle())
                .append("\n\tIssue: ").append(issueNumber)
                .append("\n\tPublication Date: ").append(publicationDate)
                .append("\n\tPublisher: ").append(publisher)
                .append("\n\tAvailable: ").append(getAvailable())
                .toString();
    }

}
