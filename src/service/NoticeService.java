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
}
