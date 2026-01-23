package entites;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final int MAX_BORROW_DAY = 14;
    public static final double DAILY_LATE_PENALTY = 500D;

    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(MAX_BORROW_DAY);
        returnDate = null;

        book.borrowBook();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    public void returnBook() {
        if (returnDate != null) {
            throw new IllegalStateException("Book already returned");
        }
        returnDate = LocalDate.now();
        book.returnBook();
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
        sb.append("  Book: ").append(book.getTitle()).append("\n");
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
