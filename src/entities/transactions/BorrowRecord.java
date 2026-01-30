package entities.transactions;

import entities.items.LibraryItem;
import entities.people.Member;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final int DEFAULT_BORROW_DAY = 14;
    public static final double DAILY_LATE_PENALTY = 500D;

    private LibraryItem item;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(LibraryItem item, Member member) {
        this.item = item;
        this.member = member;
        this.borrowDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(DEFAULT_BORROW_DAY);
        returnDate = null;

        item.borrowItem();
    }

    public LibraryItem getItem() {
        return item;
    }

    public void setItem(LibraryItem item) {
        this.item = item;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void returnItem() {
        if (returnDate != null) {
            throw new IllegalStateException("Book already returned");
        }
        returnDate = LocalDate.now();
        item.returnItem();
    }

    public boolean isOverdue() {
        return getReturnedEffectiveDate().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue())
            return 0;

        return ChronoUnit.DAYS.between(dueDate, getReturnedEffectiveDate());
    }

    public long getBorrowDurationInDays() {
        return ChronoUnit.DAYS.between(borrowDate, getReturnedEffectiveDate());
    }

    private LocalDate getReturnedEffectiveDate() {
        return returnDate != null ? returnDate : LocalDate.now();
    }

    public double calculateFine() {
        if (!isOverdue())
            return 0;

        return DAILY_LATE_PENALTY * getDaysOverdue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Borrow Record:\n");
        sb.append("  Book: ").append(item.getTitle()).append("\n");
        sb.append("  Member: ").append(member.getName()).append("\n");
        sb.append("  Borrowed: ").append(borrowDate.format(FORMATTER)).append("\n");
        sb.append("  Due: ").append(dueDate.format(FORMATTER)).append("\n");

        if (returnDate != null) {
            sb.append("  Returned: ").append(returnDate.format(FORMATTER)).append("\n");
        } else if (isOverdue()) {
            sb.append("  Status: OVERDUE by ").append(getDaysOverdue()).append(" days\n");
            sb.append("  Fine: ").append(String.format("%.2f", calculateFine())).append(" Tomans\n");
        } else {
            long daysLeft = Period.between(LocalDate.now(), dueDate).getDays();
            sb.append("  Status: Due in ").append(daysLeft).append(" days\n");
        }

        return sb.toString();
    }
}
