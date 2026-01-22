import entites.Book;
import entites.Library;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook(new Book("1", "A", "mr A"));
        library.addBook(new Book("2", "B", "mr B"));
        library.addBook(new Book("3", "C", "mr C"));
        library.addBook(new Book("4", "D", "mr D"));
        library.addBook(new Book("5", "E", "mr E"));
        library.addBook(new Book("6", "F", "mr F"));
        library.addBook(new Book("7", "G", "mr G"));
        library.addBook(new Book("8", "H", "mr H"));
        library.addBook(new Book("9", "I", "mr I"));
        library.addBook(new Book("10", "J", "mr J"));


        Book book = library.searchBook("A", "mr A");

        library.addBook(new Book("1", "A", "mr A"));
        Book book2 = library.searchBook("X", "mr X");
        book.borrowBook();

        System.out.println(book);
    }
}