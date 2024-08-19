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
 * InquiryDao 클래스는 문의글 데이터를 관리합니다.
 */
public class InquiryDao {

  public void save(Inquiry inquiry) {
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
    } catch (SQLException e) {
      throw new RuntimeException("문의글 저장 중 데이터베이스 오류 발생했습니다" + e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("문의글 저장 중 알 수 없는 오류 발생했습니다" + e.getMessage());
    }
  }

  public List<Inquiry> findAll() {
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
    } catch (SQLException e) {
      throw new RuntimeException("문의글 목록 조회 중 데이터베이스 오류 발생했습니다:" + e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("문의글 목록 조회 중 알 수 없는 오류 발생했습니다:"+ e.getMessage());
    }
    return inquiries;
  }

  public void update(Inquiry inquiry) {
    String sql = "UPDATE inquiry SET title = ?, content = ? WHERE id = ? AND inquiry_pw = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, inquiry.getTitle());
      pstmt.setString(2, inquiry.getContent());
      pstmt.setInt(3, inquiry.getId());
      pstmt.setString(4, inquiry.getInquiryPassword());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("문의글 수정 중 데이터베이스 오류 발생했습니다:"+ e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("문의글 수정 중 오류 발생했습니다:"+ e.getMessage());
    }
  }

  public void delete(Integer id, String password) {
    String sql = "DELETE FROM inquiry WHERE id = ? AND inquiry_pw = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.setString(2, password);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("문의글 삭제 중 데이터베이스 오류 발생했습니다:"+ e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("문의글 삭제 중 알 수 없는 오류 발생했습니다:"+ e.getMessage());
    }
  }

  public void deleteByAdmin(Integer id) {
    String sql = "DELETE FROM inquiry WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("관리자에 의한 문의글 삭제 중 데이터베이스 오류 발생했습니다:"+ e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("관리자에 의한 문의글 삭제 중 알 수 없는 오류 발생했습니다:"+ e.getMessage());
    }
  }

  public Inquiry findById(Integer id) {
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
    } catch (SQLException e) {
      throw new RuntimeException("문의글 조회 중 데이터베이스 오류 발생했습니다:"+ e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("문의글 조회 중 오류 발생했습니다:"+ e.getMessage());
    }
  }
}