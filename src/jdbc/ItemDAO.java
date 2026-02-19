package jdbc;

import entities.items.*;
import enums.LibraryItemType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDAO implements AutoCloseable {
    private final DatabaseManager dbManager;

    public ItemDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void insertItem(LibraryItem item) throws SQLException {
        String sql = "INSERT INTO items (id, title, type, available) VALUES (?, ?, ?, ?)";

        try (var pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, item.getItemType().name());
            pstmt.setBoolean(4, item.getAvailable());
            pstmt.executeUpdate();
        }
    }

    public Optional<LibraryItem> findById(String id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                if (rs.next()) {
                    return Optional.of(mapToItem(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<LibraryItem> findAll() throws SQLException {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY title";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs == null)
                throw new SQLException("executeQuery returned null - check driver or connection");

            while (rs.next()) {
                items.add(mapToItem(rs));
            }
        }
        return items;
    }

    public List<LibraryItem> findByType(LibraryItemType type) throws SQLException {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE type = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, type.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                while (rs.next()) {
                    items.add(mapToItem(rs));
                }
            }
        }
        return items;
    }

    public boolean updateAvailability(String id, boolean available) throws SQLException {
        String sql = "UPDATE items SET available = ? WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setBoolean(1, available);
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(String id) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";


        try (var pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }

    }

    public int countByType(LibraryItemType type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM items WHERE type = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, type.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                if (rs.next())
                    return rs.getInt(1);
            }

        }

        return 0;
    }

    private LibraryItem mapToItem(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        LibraryItemType type = LibraryItemType.valueOf(rs.getString("type"));
        boolean available = rs.getBoolean("available");

        String key = id.substring(id.indexOf('-') + 1);

        LibraryItem item = switch (type) {
            case BOOK -> new Book(key, title, "Unknown Author");
            case MAGAZINE -> new Magazine(title, key, rs.getDate("created_at").toLocalDate());
            case DVD -> new DVD(key, title, "Unknown Director");
            case REFERENCE_BOOK -> new ReferenceBook(key, title, "General");
            case AUDIO_BOOK -> new AudioBook(key, title);
        };

        item.setAvailable(available);
        return item;
    }

    @Override
    public void close() throws Exception {
        // Nothing to close, dbManager handles connection
    }
}
