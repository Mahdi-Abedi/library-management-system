package exceptions;

public class BorrowException extends RuntimeException{
    private final String itemId;
    private final int memberId;

    public BorrowException(String message, String itemId, int memberId) {
        super(message);
        this.itemId = itemId;
        this.memberId = memberId;
    }

    public BorrowException(String message, Throwable cause, String itemId, int memberId) {
        super(message, cause);
        this.itemId = itemId;
        this.memberId = memberId;
    }

    public String getItemId() {
        return itemId;
    }

    public int getMemberId() {
        return memberId;
    }

    @Override
    public String getMessage() {
        return String.format("Borrow failed for item %s by member %d: %s",
                itemId, memberId, super.getMessage());    }
}
