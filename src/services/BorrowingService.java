package services;

import entities.items.Book;
import entities.people.Member;
import entities.transactions.BorrowRecord;

public class BorrowingService {
    public BorrowRecord borrowBook(Book book, Member member, int days) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double calculateFine(BorrowRecord record) {
        throw new UnsupportedOperationException("Not supported yet.");
        //return 0;
    }
}
