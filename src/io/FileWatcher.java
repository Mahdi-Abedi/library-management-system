package io;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileWatcher implements AutoCloseable {
    private final WatchService watchService;
    private final Path directory;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public FileWatcher(String directoryPath) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.directory = Paths.get(directoryPath);
        this.executor = Executors.newSingleThreadExecutor();

        directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        startWatching();
    }

    private void startWatching() {
        executor.submit(() -> {
            while (running) {
                try {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();

                        handleEvent(kind, filename);
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void handleEvent(WatchEvent.Kind<?> kind, Path filename) {
        String eventType = kind == StandardWatchEventKinds.ENTRY_CREATE ? "CREATED" :
                kind == StandardWatchEventKinds.ENTRY_DELETE ? "DELETED" :
                        kind == StandardWatchEventKinds.ENTRY_MODIFY ? "MODIFIED" : "UNKNOWN";

        System.out.println("File " + filename + " was " + eventType);
    }

    @Override
    public void close() {
        running = false;
        executor.shutdown();
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}