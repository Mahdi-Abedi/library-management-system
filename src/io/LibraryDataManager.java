package io;

import entities.Library;
import entities.items.LibraryItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryDataManager {
    private final Path exportDirectory;
    private final Path backupDirectory;
    private final FileHandler fileHandler;

    public LibraryDataManager(String baseDirectory) throws IOException {
        this.exportDirectory = Paths.get(baseDirectory, "exports");
        this.backupDirectory = Paths.get(baseDirectory, "backups");
        this.fileHandler = new FileHandler(baseDirectory + "/items");

        Files.createDirectories(exportDirectory);
        Files.createDirectories(backupDirectory);
    }

    public void exportToCSV(Library library, String filename) throws IOException {
        Path csvPath = exportDirectory.resolve(filename);

        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            writer.write("ID,Title,Type,Available");
            writer.newLine();

            for (LibraryItem item : library.getAllItems()) {
                writer.write(String.format("%s,%s,%s,%s",
                        escapeCSV(item.getId()),
                        escapeCSV(item.getTitle()),
                        item.getItemType(),
                        item.getAvailable()));
                writer.newLine();
            }
        }
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public List<String[]> importFromCSV(String filename) throws IOException {
        Path csvPath = exportDirectory.resolve(filename);

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            return reader.lines()
                    .skip(1) // skip header
                    .map(line -> line.split(","))
                    .collect(Collectors.toList());
        }
    }

    public void createBackup() throws IOException {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path backupFolder = backupDirectory.resolve("backup-" + timestamp);
        Files.createDirectories(backupFolder);

        List<Path> itemFiles = fileHandler.listAllItemFiles();
        for (Path file : itemFiles) {
            Path target = backupFolder.resolve(file.getFileName());
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
        }

        Path infoFile = backupFolder.resolve("backup-info.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(infoFile)) {
            writer.write("Backup created: " + LocalDateTime.now());
            writer.newLine();
            writer.write("Items backed up: " + itemFiles.size());
        }
    }

    public List<Path> findFiles(String pattern) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        try (var stream = Files.walk(exportDirectory)) {
            return stream.filter(matcher::matches)
                    .collect(Collectors.toList());
        }
    }

    public void processFileLines(String filename, java.util.function.Consumer<String> lineProcessor)
            throws IOException {
        Path filePath = exportDirectory.resolve(filename);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineProcessor.accept(line);
            }
        }
    }
}