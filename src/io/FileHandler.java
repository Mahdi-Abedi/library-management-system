package io;

import entities.items.LibraryItem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileHandler {
    private final Path dataDirectory;

    public FileHandler(String dataDirectory) throws IOException {
        this.dataDirectory = Paths.get(dataDirectory);

        if (!Files.exists(this.dataDirectory)) {
            Files.createDirectories(this.dataDirectory);
        }
    }

    public void saveItem(LibraryItem item) throws IOException {
        Path itemFile = dataDirectory.resolve(item.getId() + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(itemFile)) {
            writer.write("ID: " + item.getId());
            writer.newLine();
            writer.write("Title: " + item.getTitle());
            writer.newLine();
            writer.write("Type: " + item.getItemType());
            writer.newLine();
            writer.write("Available: " + item.getAvailable());
            writer.newLine();
        }
    }

    public List<String> readItem(String id) throws IOException {
        Path itemFile = dataDirectory.resolve(id + ".txt");
        return Files.readAllLines(itemFile);
    }

    public boolean deleteItemFile(String id) throws IOException {
        Path itemFile = dataDirectory.resolve(id + ".txt");
        return Files.deleteIfExists(itemFile);
    }

    public List<Path> listAllItemFiles() throws IOException {
        try (var stream = Files.list(dataDirectory)) {
            return stream.filter(p -> p.toString().endsWith(".txt"))
                    .collect(Collectors.toList());
        }
    }
}
