package exceptions;

public class ItemNotAvailableException extends BorrowException{

    public ItemNotAvailableException(String itemId, int memberId) {
        super("Item is not available for borrowing", itemId, memberId);
    }
}
