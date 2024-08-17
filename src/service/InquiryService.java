package service;

import dao.InquiryDao;
import domain.Inquiry;
import java.sql.SQLException;
import java.util.List;

public class InquiryService {

  private final InquiryDao inquiryDao = new InquiryDao();

  public void addInquiry(String title, String content, String password) throws SQLException {
    Inquiry inquiry = new Inquiry(title, content, false, password, 2, null); // roleId 2: 회원, guestId 없음
    inquiryDao.save(inquiry);
  }

  public void viewAllInquiries() throws SQLException {
    List<Inquiry> inquiries = inquiryDao.findAll();
    for (Inquiry inquiry : inquiries) {
      System.out.println("ID: " + inquiry.getId() + ", 제목: " + inquiry.getTitle() + ", 내용: " + inquiry.getContent());
    }
  }

  public void viewAllPrivateInquiries() throws SQLException {
    List<Inquiry> inquiries = inquiryDao.findAll();
    for (Inquiry inquiry : inquiries) {
      if (inquiry.isPrivate()) {
        System.out.println("ID: " + inquiry.getId() + ", 제목: " + inquiry.getTitle() + ", 내용: " + inquiry.getContent());
      }
    }
  }

  public void updateInquiry(Integer id, String title, String content, String password) throws SQLException {
    Inquiry inquiry = inquiryDao.findById(id);
    if (!inquiry.getInquiryPassword().equals(password)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    inquiry.setTitle(title);
    inquiry.setContent(content);
    inquiryDao.update(inquiry);
  }
}
