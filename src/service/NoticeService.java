package service;

import dao.NoticeDao;
import domain.Notice;
import java.util.List;

public class NoticeService {

  private final NoticeDao noticeDao = new NoticeDao();

  public void addNotice(String title, String content) {
    try {
      Notice notice = new Notice(title, content, 1); // roleId 1: 관리자
      noticeDao.save(notice);
    } catch (RuntimeException e) {
      System.err.println("공지사항 등록 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void viewAllNotices() {
    try {
      List<Notice> notices = noticeDao.findAll();
      for (Notice notice : notices) {
        System.out.println("ID: " + notice.getId() + ", 제목: " + notice.getTitle() + ", 내용: " + notice.getContent());
      }
    } catch (RuntimeException e) {
      System.err.println("공지사항 조회 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void updateNotice(Integer id, String title, String content) {
    try {
      Notice notice = new Notice(title, content, 1);
      notice.setId(id);
      noticeDao.update(notice);
    } catch (RuntimeException e) {
      System.err.println("공지사항 수정 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void deleteNotice(Integer id) {
    try {
      noticeDao.delete(id);
    } catch (RuntimeException e) {
      System.err.println("공지사항 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}