package dao;

import connection.HikariCpDBConnectionUtil;
import domain.Inquiry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * InquiryRepository 클래스는 문의글 데이터를 관리합니다.
 */
public class InquiryDao {

  public void save(Inquiry inquiry) throws SQLException {
    String sql = "INSERT INTO inquiry (title, content, create_date, isPrivate, inquiry_pw, role_id, guest_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, inquiry.getTitle());
      pstmt.setString(2, inquiry.getContent());
      pstmt.setTimestamp(3, Timestamp.valueOf(inquiry.getCreateDate()));
      pstmt.setBoolean(4, inquiry.isPrivate());
      pstmt.setString(5, inquiry.getInquiryPassword());
      pstmt.setInt(6, inquiry.getRoleId());
      pstmt.setInt(7, inquiry.getGuestId());
      pstmt.executeUpdate();
    }
  }

  public List<Inquiry> findAll() throws SQLException {
    String sql = "SELECT * FROM inquiry";
    List<Inquiry> inquiries = new ArrayList<>();
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Inquiry inquiry = new Inquiry(
            rs.getString("title"),
            rs.getString("content"),
            rs.getBoolean("isPrivate"),
            rs.getString("inquiry_pw"),
            rs.getInt("role_id"),
            rs.getInt("guest_id")
        );
        inquiry.setId(rs.getInt("id"));
        inquiry.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        inquiries.add(inquiry);
      }
    }
    return inquiries;
  }

  public void update(Inquiry inquiry) throws SQLException {
    String sql = "UPDATE inquiry SET title = ?, content = ? WHERE id = ? AND inquiry_pw = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, inquiry.getTitle());
      pstmt.setString(2, inquiry.getContent());
      pstmt.setInt(3, inquiry.getId());
      pstmt.setString(4, inquiry.getInquiryPassword());
      pstmt.executeUpdate();
    }
  }

  public void delete(Integer id, String password) throws SQLException {
    String sql = "DELETE FROM inquiry WHERE id = ? AND inquiry_pw = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.setString(2, password);
      pstmt.executeUpdate();
    }
  }

  public void deleteByAdmin(Integer id) throws SQLException {
    String sql = "DELETE FROM inquiry WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    }
  }

  public Inquiry findById(Integer id) throws SQLException {
    String sql = "SELECT * FROM inquiry WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          Inquiry inquiry = new Inquiry(
              rs.getString("title"),
              rs.getString("content"),
              rs.getBoolean("isPrivate"),
              rs.getString("inquiry_pw"),
              rs.getInt("role_id"),
              rs.getInt("guest_id")
          );
          inquiry.setId(rs.getInt("id"));
          inquiry.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
          return inquiry;
        } else {
          throw new SQLException("해당 ID의 문의글이 존재하지 않습니다.");
        }
      }
    }
  }
}
