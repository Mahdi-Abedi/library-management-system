package services;

import entities.Library;
import entities.items.LibraryItem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class LibraryExporter {

    public void exportToFile(Library library, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write("Library Export - " + LocalDateTime.now());
            writer.newLine();
            writer.write("=".repeat(50));
            writer.newLine();

            for (LibraryItem item : library.getAllItems()) {
                writer.write(formatItem(item));
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            throw new IOException("Cannot write to file: " + filePath, e);
        } catch (SecurityException e) {
            throw new IOException("Security exception when accessing file", e);
        }
    }

    private String formatItem(LibraryItem item) {
        return String.format("%s | %s | %s | %s",
                item.getId(),
                item.getTitle(),
                item.getItemType(),
                item.getAvailable() ? "Available" : "Borrowed");
    }

    public void exportWithAutoClose(Library library, String filePath) {
        try (var resources = new ExportResources(filePath)) {
            BufferedWriter writer = resources.getWriter();
            exportLibraryData(library, writer);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }

    private void exportLibraryData(Library library, BufferedWriter writer) throws IOException {
        writer.write("Library Export - " + LocalDateTime.now());
        writer.newLine();
        writer.write("=".repeat(50));
        writer.newLine();

        List<LibraryItem> items = library.getAllItems();
        for (LibraryItem item : items) {
            writer.write(formatItem(item));
            writer.newLine();
        }

        writer.write("=".repeat(50));
        writer.newLine();
        writer.write("Total items: " + items.size());
        writer.newLine();
        writer.write("Export completed at: " + LocalDateTime.now());
    }

    private static class ExportResources implements AutoCloseable {
        private final BufferedWriter writer;
        private final FileOutputStream fos;

        public ExportResources(String filePath) throws IOException {
            this.fos = new FileOutputStream(filePath);
            this.writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
        }

        public BufferedWriter getWriter() { return writer; }

        @Override
        public void close() throws IOException {
            try {
                writer.close();
            } finally {
                fos.close();
            }
        }
    }
}
