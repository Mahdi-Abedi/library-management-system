package services;

import entities.items.LibraryItem;
import entities.people.Member;
import entities.transactions.BorrowRecord;
import enums.ItemStatus;
import interfaces.LoanPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowingService {
    private final BorrowingConfig config;
    private final List<BorrowRecord> activeRecords = new ArrayList<>();

    public BorrowingService() {
        this.config = new BorrowingConfig.Builder().build();
    }

    public BorrowingService(BorrowingConfig config) {
        this.config = config;
    }

    public BorrowResult borrowItem(LibraryItem item, Member member, Integer customDays) {
        BorrowResult validateResult = validateBorrow(item, member, customDays);
        if (validateResult != null)
            return validateResult;

        if (!config.isAllowMultipleBorrows()) {
            long activeCount = activeRecords.stream()
                    .filter(r -> r.getMember().equals(member) && r.getReturnDate() == null)
                    .count();
            if (activeCount >= config.getMaxBorrowsPerMember()) {
                return BorrowResult.failure("Member has reached maximum borrow limit");
            }
        }

        item.setAvailable(false);
        item.setStatus(ItemStatus.BORROWED);

        BorrowRecord record = createBorrowRecord(item, member, customDays);

        if (item instanceof LoanPolicy policy) {
            record.setDueDate(record.getBorrowDate().plusDays(policy.getMaxLoanDays()));
        }
        activeRecords.add(record);

        return BorrowResult.success(record);
    }

    private BorrowResult validateBorrow(LibraryItem item, Member member, Integer customDays) {
        if (item == null)
            return BorrowResult.failure("Item is null");

        if (member == null)
            return BorrowResult.failure("Member is null");

        if (!item.canBeBorrowed())
            return BorrowResult.failure("Item cannot be borrowed");

        if (customDays != null) {
            if (customDays <= 0)
                return BorrowResult.failure("Borrow days must be positive");
            else if (customDays > 30)
                return BorrowResult.failure("Maximum borrow period is 30 days");
        }

        return null;
    }

    public BorrowResult borrowItem(LibraryItem item, Member member) {
        return borrowItem(item, member, null);
    }

    public Optional<BorrowRecord> returnItem(LibraryItem item) {
        Optional<BorrowRecord> recordOpt = findActiveRecord(item);

        recordOpt.ifPresent(record -> {
            item.setAvailable(true);
            item.setStatus(ItemStatus.AVAILABLE);
            record.setReturnDate(LocalDate.now());
            activeRecords.remove(record);
        });

        return recordOpt;
    }

    public boolean renewBorrow(BorrowRecord record, int additionalDays) {
        if (record.getReturnDate() != null)
            return false;

        if (!(record.getItem() instanceof LoanPolicy policy) || !policy.isRenewable())
            return false;

        LocalDate newDueDate = record.getDueDate().plusDays(additionalDays);
        record.setDueDate(newDueDate);

        return true;
    }

    public double calculateFine(BorrowRecord record) {
        if (record.getReturnDate() == null || !record.isOverdue())
            return 0.0;

        long daysOverdue = record.getDaysOverdue();

        if (record.getItem() instanceof LoanPolicy policy)
            return daysOverdue * policy.getDailyFine();

        return daysOverdue * config.getDefaultDailyFine();
    }

    private Optional<BorrowRecord> findActiveRecord(LibraryItem item) {
        return activeRecords.stream()
                .filter(r -> r.getItem().equals(item) && r.getReturnDate() == null)
                .findFirst();
    }

    private BorrowRecord createBorrowRecord(LibraryItem item, Member member, Integer customDays) {
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate;

        if (item instanceof LoanPolicy policy) {
            int loanDays = (customDays != null) ? customDays : policy.getMaxLoanDays();
            dueDate = borrowDate.plusDays(loanDays);
        } else {
            int loanDays = (customDays != null) ? customDays : config.getDefaultLoanDays();
            dueDate = borrowDate.plusDays(loanDays);
        }

        BorrowRecord record = new BorrowRecord();
        record.setItem(item);
        record.setMember(member);
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);

        return record;
    }

    public List<BorrowRecord> getActiveBorrows() {
        return new ArrayList<>(activeRecords);
    }

    public List<BorrowRecord> getMemberActiveBorrows(Member member) {
        return activeRecords.stream()
                .filter(r -> r.getMember().equals(member) && r.getReturnDate() == null)
                .toList();
    }

    public List<BorrowRecord> getOverdueBorrows() {
        return activeRecords.stream()
                .filter(BorrowRecord::isOverdue)
                .toList();
    }

    public static class BorrowResult {
        private final boolean success;
        private final BorrowRecord record;
        private final String message;

        public BorrowResult(boolean success, BorrowRecord record, String message) {
            this.success = success;
            this.record = record;
            this.message = message;
        }

        public static BorrowResult success(BorrowRecord record) {
            return new BorrowResult(true, record, "Borrow successful");
        }

        public static BorrowResult failure(String message) {
            return new BorrowResult(false, null, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public BorrowRecord getRecord() {
            return record;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class BorrowingConfig {
        private int defaultLoanDays = 14;
        private double defaultDailyFine = 500.0;
        private boolean allowMultipleBorrows = true;
        private int maxBorrowsPerMember = 5;

        public int getDefaultLoanDays() {
            return defaultLoanDays;
        }

        public double getDefaultDailyFine() {
            return defaultDailyFine;
        }

        public boolean isAllowMultipleBorrows() {
            return allowMultipleBorrows;
        }

        public int getMaxBorrowsPerMember() {
            return maxBorrowsPerMember;
        }

        public static class Builder {
            private final BorrowingConfig config = new BorrowingConfig();

            public Builder defaultLoanDays(int days) {
                config.defaultLoanDays = days;
                return this;
            }

            public Builder defaultDailyFine(double fine) {
                config.defaultDailyFine = fine;
                return this;
            }

            public Builder allowMultipleBorrows(boolean allow) {
                config.allowMultipleBorrows = allow;
                return this;
            }

            public Builder maxBorrowsPerMember(int max) {
                config.maxBorrowsPerMember = max;
                return this;
            }

            public BorrowingConfig build() {
                return config;
            }
        }
    }
}
