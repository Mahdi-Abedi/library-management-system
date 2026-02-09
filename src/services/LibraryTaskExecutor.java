package services;

import entities.Library;
import entities.items.LibraryItem;
import entities.people.Member;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LibraryTaskExecutor {

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Library library;

    public LibraryTaskExecutor(Library library) {
        this.library = library;

        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new LibraryTreadFactory()
        );
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    public CompletableFuture<List<LibraryItem>> processItemsAsync(List<LibraryItem> items) {
        return CompletableFuture.supplyAsync(() -> items.parallelStream()
                .peek(item -> System.out.println(Thread.currentThread().getName() + " processing: " + item.getTitle()))
                .toList(), executorService);
    }

    public CompletableFuture<Boolean> borrowMultipleAsync(Member member, List<String> itemIds) {
        List<CompletableFuture<Boolean>> futures = itemIds.stream()
                .map(itemId ->
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                var result = library.borrowItem(itemId, member);
                                return result.isSuccess();
                            } catch (Exception e) {
                                return false;
                            }
                        }, executorService)
                )
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream()
                        .allMatch(CompletableFuture::join));
    }

    public void scheduleOverdueCheck(long initialDelay, long period, TimeUnit unit) {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("Checking overdue items...");

        }, initialDelay, period, unit);
    }

    public void shutdown() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
            if (!scheduledExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }

    private static class LibraryTreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "library-worker-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
