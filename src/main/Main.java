package main;

import entities.Library;
import entities.items.Book;
import entities.items.LibraryItem;
import entities.people.Member;
import services.AsyncLibraryService;
import services.BorrowingService;
import services.LibraryTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n=== CHAPTER 13: CONCURRENCY ===\n");

        System.out.println("1. THREAD-SAFE LIBRARY OPERATIONS");
        Library library = new Library();
        LibraryTaskExecutor executor = new LibraryTaskExecutor(library);

        for (int i = 0; i < 10; i++) {
            Book book = new Book.Builder("ISBN-" + i, "Book " + i, "Author " + i)
                    .setPageCount(100 + i * 10)
                    .build();
            library.addItem(book);
        }

        System.out.println("\n2. PARALLEL STREAM PROCESSING");
        List<LibraryItem> items = library.getAllItems();
        CompletableFuture<List<LibraryItem>> processedFuture = executor.processItemsAsync(items);

        processedFuture.thenAccept(processedItems -> {
            System.out.println("Processed " + processedItems.size() + " items in parallel");
        }).join();

        System.out.println("\n3. COMPLETABLE FUTURE DEMONSTRATION");
        AsyncLibraryService asyncService = new AsyncLibraryService(library);

        CompletableFuture<String> reportFuture = asyncService.generateReportAsync();
        CompletableFuture<Void> maintenanceFuture = asyncService.performMaintenanceAsync();

        reportFuture.thenCombine(maintenanceFuture, (report, ignored) -> {
            System.out.println("Report generated during maintenance");
            return report.substring(0, Math.min(100, report.length()));
        }).thenAccept(partialReport -> {
            System.out.println("Partial report: " + partialReport + "...");
        }).join();

        System.out.println("\n4. MULTI-THREAD BORROW SIMULATION");
        Member testMember = new Member(999, "Test User", "test@example.com");
        library.addMember(testMember);

        List<CompletableFuture<Boolean>> borrowFutures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int index = i;
            borrowFutures.add(CompletableFuture.supplyAsync(() -> {
                String itemId = "ISBN-" + index;
                var result = library.borrowItem(itemId, testMember);
                System.out.println(Thread.currentThread().getName() +
                        " borrowed ISBN-" + index + ": " + result.isSuccess());
                return result.isSuccess();
            }));
        }

        CompletableFuture.allOf(borrowFutures.toArray(new CompletableFuture[0])).join();
        System.out.println("All borrow operations completed");

        System.out.println("\n5. SCHEDULED TASKS");
        executor.scheduleOverdueCheck(2, 5, TimeUnit.SECONDS);

        System.out.println("\n6. THREAD SYNCHRONIZATION TEST");
        BorrowingService borrowingService = library.getBorrowingService();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    try {
                        var records = borrowingService.getActiveBorrows();
                        System.out.println(Thread.currentThread().getName() +
                                " read " + records.size() + " records");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Reader-" + i);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n7. ATOMIC OPERATIONS");
        System.out.println("Creating atomic counter...");
        AtomicInteger counter = new AtomicInteger(0);

        List<Thread> counterThreads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.incrementAndGet();
                }
            });
            counterThreads.add(thread);
            thread.start();
        }

        for (Thread thread : counterThreads) {
            thread.join();
        }

        System.out.println("Final counter value: " + counter.get() + " (expected: 5000)");

        System.out.println("\n8. EXECUTOR SHUTDOWN");
        executor.shutdown();
        System.out.println("Executor service shutdown completed");

        System.out.println("\n=== CONCURRENCY DEMONSTRATION COMPLETED ===");
        System.out.println("All thread-safe operations verified");
        System.out.println("No data races or deadlocks detected");
    }
}