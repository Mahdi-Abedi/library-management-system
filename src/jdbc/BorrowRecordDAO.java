package jdbc;

import entities.items.LibraryItem;
import entities.people.Member;
import entities.transactions.BorrowRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowRecordDAO implements AutoCloseable {
    private final DatabaseManager dbManager;
    private final ItemDAO itemDAO;
    private final MemberDAO memberDAO;

    public BorrowRecordDAO(DatabaseManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        this.itemDAO = new ItemDAO(dbManager);
        this.memberDAO = new MemberDAO(dbManager);
    }

    public void insertRecord(BorrowRecord record) throws SQLException {
        String sql = "INSERT INTO borrow_records (item_id, member_id, borrow_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, record.getItem().getId());
            pstmt.setInt(2, record.getMember().getId());
            pstmt.setDate(3, Date.valueOf(record.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(record.getDueDate()));
            pstmt.setDate(5, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                }
            }
        }
    }

    public List<BorrowRecord> findActiveRecords() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE return_date IS NULL ORDER BY due_date";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs == null)
                throw new SQLException("executeQuery returned null - check driver or connection");

            while (rs.next()) {
                records.add(mapToRecord(rs));
            }
        }
        return records;
    }

    public List<BorrowRecord> findByMember(int memberId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE member_id = ? ORDER BY borrow_date DESC";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                while (rs.next()) {
                    records.add(mapToRecord(rs));
                }
            }
        }
        return records;
    }

    public Optional<BorrowRecord> findActiveByItem(String itemId) throws SQLException {
        String sql = "SELECT * FROM borrow_records WHERE item_id = ? AND return_date IS NULL";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, itemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                if (rs.next()) {
                    return Optional.of(mapToRecord(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<BorrowRecord> findOverdueRecords() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE return_date IS NULL AND due_date < CURRENT_DATE";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs == null)
                throw new SQLException("executeQuery returned null - check driver or connection");

            while (rs.next()) {
                records.add(mapToRecord(rs));
            }
        }
        return records;
    }

    public boolean returnItem(String itemId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE borrow_records SET return_date = ? WHERE item_id = ? AND return_date IS NULL";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setString(2, itemId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                itemDAO.updateAvailability(itemId, true);
            }

            return updated;
        }
    }

    private BorrowRecord mapToRecord(ResultSet rs) throws SQLException {
        String itemId = rs.getString("item_id");
        int memberId = rs.getInt("member_id");

        Optional<LibraryItem> itemOpt = itemDAO.findById(itemId);
        Optional<Member> memberOpt = memberDAO.findById(memberId);

        if (itemOpt.isEmpty() || memberOpt.isEmpty()) {
            throw new SQLException("Referenced item or member not found");
        }
        
        BorrowRecord record = new BorrowRecord();
        record.setItem(itemOpt.get());
        record.setMember(memberOpt.get());
        record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        record.setDueDate(rs.getDate("due_date").toLocalDate());

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDate());
        }

        return record;
    }

    @Override
    public void close() throws Exception {
        // Nothing to close
    }
}