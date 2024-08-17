package service;

import dao.NoticeDao;
import domain.Notice;
import java.sql.SQLException;
import java.util.List;

public class NoticeService {

  private final NoticeDao noticeDao = new NoticeDao();

  public void addNotice(String title, String content) throws SQLException {
    Notice notice = new Notice(title, content, 1); // roleId 1: 관리자
    noticeDao.save(notice);
  }

  public void viewAllNotices() throws SQLException {
    List<Notice> notices = noticeDao.findAll();
    for (Notice notice : notices) {
      System.out.println("ID: " + notice.getId() + ", 제목: " + notice.getTitle() + ", 내용: " + notice.getContent());
    }
  }

  public void updateNotice(Integer id, String title, String content) throws SQLException {
    Notice notice = new Notice(title, content, 1);
    notice.setId(id);
    noticeDao.update(notice);
  }
}
