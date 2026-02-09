package services;

import entities.Library;
import entities.items.LibraryItem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AsyncLibraryService {
    private final Library library;

    public AsyncLibraryService(Library library) {
        this.library = library;
    }

    public CompletableFuture<LibraryItem> findItemByIdAsync(String id) {
        return CompletableFuture.supplyAsync(() ->
                library.findItemById(id)
                        .orElseThrow(() -> new RuntimeException("Item not found: " + id))
        );
    }

    public CompletableFuture<String> generateReportAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return library.generateLibraryReport();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).exceptionally(ex -> {
            System.err.println("Report generation failed: " + ex.getMessage());
            return "Error generating report";
        });
    }

    public CompletableFuture<Void> performMaintenanceAsync() {
        return CompletableFuture.runAsync(() -> {
            System.out.println("Starting maintenance...");
        }).thenRunAsync(() -> {
            System.out.println("Maintenance completed");
        }).thenRunAsync(() -> {
            System.out.println("Sending notification...");
        });
    }

    public CompletableFuture<LibraryItem> findItemWithTimeout(String id, long timeout) {
        return findItemByIdAsync(id)
                .orTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof java.util.concurrent.TimeoutException) {
                        System.out.println("Search timed out for: " + id);
                    }
                    return null;
                });
    }
}
