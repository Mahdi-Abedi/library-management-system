// File: jdbc/MemberDAO.java
package jdbc;

import entities.people.Member;
import enums.MemberStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAO implements AutoCloseable {
    private final DatabaseManager dbManager;

    public MemberDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void insertMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (id, name, email, phone, status, membership_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getPhoneNumber());
            pstmt.setString(5, member.getStatus().name());
            pstmt.setDate(6, Date.valueOf(member.getMembershipDate()));
            pstmt.executeUpdate();
        }
    }
    
    public Optional<Member> findById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                if (rs.next()) {
                    return Optional.of(mapToMember(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Member> findAll() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs == null)
                throw new SQLException("executeQuery returned null - check driver or connection");

            while (rs.next()) {
                members.add(mapToMember(rs));
            }
        }
        return members;
    }

    public List<Member> findByStatus(MemberStatus status) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE status = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs == null)
                    throw new SQLException("executeQuery returned null - check driver or connection");

                while (rs.next()) {
                    members.add(mapToMember(rs));
                }
            }
        }
        return members;
    }

    public boolean updateStatus(int id, MemberStatus status) throws SQLException {
        String sql = "UPDATE members SET status = ? WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Member mapToMember(ResultSet rs) throws SQLException {
        Member member = new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
        );
        member.setPhoneNumber(rs.getString("phone"));
        member.setStatus(MemberStatus.valueOf(rs.getString("status")));
        member.setMembershipDate(rs.getDate("membership_date").toLocalDate());
        return member;
    }

    @Override
    public void close() throws Exception {
        // Nothing to close
    }
}