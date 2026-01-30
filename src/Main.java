import entities.Library;
import entities.items.Book;
import entities.items.LibraryItem;
import entities.items.Magazine;
import entities.people.Member;
import enums.LibraryItemType;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== FINAL TEST - CHAPTER 6: CLASS DESIGN ===\n");
        
        Library library = new Library();

        // ۱. ایجاد آیتم‌های مختلف
        System.out.println("1. Creating different library items...");
        Book book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        book.setPageCount(416);
        book.setPublicationYear(2018);

        Magazine magazine = new Magazine("ISSUE-2024-01", "Java Monthly",
                LocalDate.of(2024, 1, 15));
        magazine.setPublisher("Java Publications");

        // ۲. اضافه کردن با polymorphism
        System.out.println("\n2. Adding items polymorphically...");
        library.addItem(book);      // Book is-a LibraryItem
        library.addItem(magazine);  // Magazine is-a LibraryItem

        // ۳. تست inheritance
        System.out.println("\n3. Testing inheritance hierarchy:");
        System.out.println("Book instanceof LibraryItem: " + (book instanceof LibraryItem));
        System.out.println("Magazine instanceof LibraryItem: " + (magazine instanceof LibraryItem));
        System.out.println("Book instanceof Object: " + (book instanceof Object));

        // ۴. تست polymorphism
        System.out.println("\n4. Testing polymorphism:");
        List<LibraryItem> items = library.getAllItems();
        for (LibraryItem item : items) {
            System.out.println("- " + item.getTitle() +
                    " | Actual type: " + item.getClass().getSimpleName() +
                    " | Item type: " + item.getItemType());
        }

        // ۵. تست interface implementation
        System.out.println("\n5. Testing interface implementation:");
        if (book instanceof interfaces.LoanPolicy bookPolicy) {
            System.out.println("Book loan policy:");
            System.out.println("  Max days: " + bookPolicy.getMaxLoanDays());
            System.out.println("  Daily fine: " + bookPolicy.getDailyFine());
            System.out.println("  Renewable: " + bookPolicy.isRenewable());
        }

        if (magazine instanceof interfaces.LoanPolicy magPolicy) {
            System.out.println("Magazine loan policy:");
            System.out.println("  Max days: " + magPolicy.getMaxLoanDays());
            System.out.println("  Daily fine: " + magPolicy.getDailyFine());
            System.out.println("  Renewable: " + magPolicy.isRenewable());
        }

        // ۶. تست abstract method
        System.out.println("\n6. Testing abstract method implementation:");
        items.forEach(item -> {
            System.out.println(item.getTitle() + " -> " + item.getItemType());
        });

        // ۷. تعداد آیتم‌ها بر اساس نوع
        System.out.println("\n7. Item count by type:");
        for (LibraryItemType type : LibraryItemType.values()) {
            long count = library.countItemsByType(type);
            System.out.println(type + ": " + count);
        }

        // ۸. تست loanable items
        System.out.println("\n8. Loanable items (available + implements LoanPolicy):");
        List<LibraryItem> loanable = library.getLoanableItems();
        System.out.println("Count: " + loanable.size());
        loanable.forEach(item ->
                System.out.println("  - " + item.getTitle())
        );

        // ۹. ایجاد یک عضو و تست امانت
        System.out.println("\n9. Testing borrowing with polymorphism:");
        Member member = new Member(101, "Ali Rezaei", "ali@example.com");
        library.addMember(member);

        // کتاب را امانت بده
        if (book.getAvailable()) {
            var record = library.borrowItem(book.getId(), member);
            System.out.println("Borrowed: " + book.getTitle());
            System.out.println("Due date: " + record.getDueDate());
            System.out.println("Book available now: " + book.getAvailable());
        }

        // ۱۰. گزارش نهایی
        System.out.println("\n10. Final library report:");
        System.out.println(library.generateLibraryReport());

        System.out.println("\n✅ CHAPTER 6 COMPLETED SUCCESSFULLY!");
    }
}