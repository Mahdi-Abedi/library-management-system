package entities.transactions;

import entities.items.LibraryItem;
import entities.people.Member;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;

public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private LibraryItem item;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public LibraryItem getItem() {
        return item;
    }

    public static Comparator<BorrowRecord> getComparatorByDueDate() {
        return Comparator.comparing(BorrowRecord::getDueDate);
    }

    public Member getMember() {
        return member;
    }

    public static Comparator<BorrowRecord> getComparatorByMemberName() {
        return Comparator.comparing(r -> r.getMember().getName(), String.CASE_INSENSITIVE_ORDER);
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setItem(LibraryItem item) {
        this.item = Objects.requireNonNull(item, "Item cannot be null");
        ;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setMember(Member member) {
        this.member = Objects.requireNonNull(member, "Member cannot be null");
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = Objects.requireNonNull(borrowDate, "Borrow date cannot be null");
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");

        if (borrowDate != null && dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Due date cannot be before borrow date");
        }
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null && borrowDate != null && returnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }
    }

    public boolean isOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }

        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();
        return ChronoUnit.DAYS.between(dueDate, endDate);
    }

    public long getBorrowDurationInDays() {
        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();
        return ChronoUnit.DAYS.between(borrowDate, endDate);
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public String generateReport() {
        class ReportFormatter {
            private final BorrowRecord record;

            public ReportFormatter(BorrowRecord record) {
                this.record = record;
            }

            String formatHeader() {
                return "=".repeat(50) + "\n" +
                        "BORROW RECORD REPORT\n" +
                        "=".repeat(50) + "\n";
            }

            String formatDetails() {
                return String.format(
                        "Item: %s (%s)\n" +
                                "Member: %s\n" +
                                "Borrowed: %s\n" +
                                "Due: %s\n" +
                                "Returned: %s\n",
                        record.item.getTitle(),
                        record.item.getItemType(),
                        record.member.getName(),
                        record.borrowDate,
                        record.dueDate,
                        record.returnDate != null ? record.returnDate.toString() : "Not yet returned"
                );
            }

            String formatStatus() {
                if (record.returnDate != null) {
                    if (record.isOverdue()) {
                        return String.format("Status: RETURNED LATE (Overdue by %d days)",
                                record.getDaysOverdue());
                    } else {
                        return "Status: RETURNED ON TIME";
                    }
                } else if (record.isOverdue()) {
                    return String.format("Status: OVERDUE (Currently %d days late)",
                            record.getDaysOverdue());
                } else {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), record.dueDate);
                    return String.format("Status: ACTIVE (Due in %d days)", daysLeft);
                }
            }
        }

        ReportFormatter formatter = new ReportFormatter(this);
        StringBuilder report = new StringBuilder();
        report.append(formatter.formatHeader());
        report.append(formatter.formatDetails());
        report.append(formatter.formatStatus());

        return report.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return Objects.equals(item, that.item) &&
                Objects.equals(member, that.member) &&
                Objects.equals(borrowDate, that.borrowDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, member, borrowDate);
    }

    @Override
    public String toString() {
        return String.format("BorrowRecord{item=%s, member=%s, borrowDate=%s, dueDate=%s, returnDate=%s}",
                item.getTitle(), member.getName(), borrowDate, dueDate, returnDate);
    }
}
