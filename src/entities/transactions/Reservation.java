package entities.transactions;

import entities.items.Book;
import entities.people.Member;

import java.time.LocalDate;

public class Reservation {
    private Book book;
    private Member member;
    private LocalDate reserveDate;
    private LocalDate expiryDate;
}