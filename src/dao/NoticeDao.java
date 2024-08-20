package dao;

import connection.HikariCpDBConnectionUtil;
import domain.Notice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * NoticeDao 클래스는 공지사항 데이터를 관리합니다.
 */
public class NoticeDao {

  public void save(Notice notice) {
    String sql = "INSERT INTO notice (title, content, create_date, role_id) VALUES (?, ?, ?, ?)";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, notice.getTitle());
      pstmt.setString(2, notice.getContent());
      pstmt.setTimestamp(3, Timestamp.valueOf(notice.getCreateDate()));
      pstmt.setInt(4, notice.getRoleId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("공지사항 저장 중 데이터베이스 오류 발생", e);
    } catch (Exception e) {
      throw new RuntimeException("공지사항 저장 중 알 수 없는 오류 발생", e);
    }
  }

  public List<Notice> findAll() {
    String sql = "SELECT * FROM notice";
    List<Notice> notices = new ArrayList<>();
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Notice notice = new Notice(
            rs.getString("title"),
            rs.getString("content"),
            rs.getInt("role_id")
        );
        notice.setId(rs.getInt("id"));
        notice.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        notices.add(notice);
      }
    } catch (SQLException e) {
      throw new RuntimeException("공지사항 목록 조회 중 데이터베이스 오류 발생", e);
    } catch (Exception e) {
      throw new RuntimeException("공지사항 목록 조회 중 알 수 없는 오류 발생", e);
    }
    return notices;
  }

  public void update(Notice notice) {
    String sql = "UPDATE notice SET title = ?, content = ? WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, notice.getTitle());
      pstmt.setString(2, notice.getContent());
      pstmt.setInt(3, notice.getId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("공지사항 수정 중 데이터베이스 오류 발생", e);
    } catch (Exception e) {
      throw new RuntimeException("공지사항 수정 중 알 수 없는 오류 발생", e);
    }
  }

  public void delete(Integer id) {
    String sql = "DELETE FROM notice WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("공지사항 삭제 중 데이터베이스 오류 발생", e);
    } catch (Exception e) {
      throw new RuntimeException("공지사항 삭제 중 알 수 없는 오류 발생", e);
    }
  }
}