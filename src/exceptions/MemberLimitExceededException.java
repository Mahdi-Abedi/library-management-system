package exceptions;

public class MemberLimitExceededException extends BorrowException {
    private final int currentCount;
    private final int maxLimit;

    public MemberLimitExceededException(String itemId, int memberId, int currentCount, int maxLimit) {
        super("Member has exceeded borrow limit", itemId, memberId);
        this.currentCount = currentCount;
        this.maxLimit = maxLimit;
    }

    @Override
    public String getMessage() {
        return String.format("%s (current: %d, limit: %d)",
                super.getMessage(), currentCount, maxLimit);
    }
}
