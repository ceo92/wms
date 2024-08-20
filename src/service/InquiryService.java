package service;

import dao.InquiryDao;
import domain.Inquiry;
import java.sql.SQLException;
import java.util.List;

public class InquiryService {

  private final InquiryDao inquiryDao = new InquiryDao();

  public void addInquiry(String title, String content, String password) {
    try {
      Inquiry inquiry = new Inquiry(title, content, false, password, 2, null); // roleId 2: 회원, guestId 없음
      inquiryDao.save(inquiry);
    } catch (RuntimeException e) {
      System.err.println("문의글 등록 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void viewAllInquiries() {
    try {
      List<Inquiry> inquiries = inquiryDao.findAll();
      for (Inquiry inquiry : inquiries) {
        System.out.println("ID: " + inquiry.getId() + ", 제목: " + inquiry.getTitle() + ", 내용: " + inquiry.getContent());
      }
    } catch (RuntimeException e) {
      System.err.println("문의글 조회 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void viewAllPrivateInquiries() {
    try {
      List<Inquiry> inquiries = inquiryDao.findAll();
      for (Inquiry inquiry : inquiries) {
        if (inquiry.isPrivate()) {
          System.out.println("ID: " + inquiry.getId() + ", 제목: " + inquiry.getTitle() + ", 내용: " + inquiry.getContent());
        }
      }
    } catch (RuntimeException e) {
      System.err.println("비밀 문의글 조회 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void updateInquiry(Integer id, String title, String content, String password) {
    try {
      Inquiry inquiry = inquiryDao.findById(id);
      if (!inquiry.getInquiryPassword().equals(password)) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
      }
      inquiry.setTitle(title);
      inquiry.setContent(content);
      inquiryDao.update(inquiry);
    } catch (IllegalArgumentException e) {
      // 비밀번호 불일치 예외 처리
      System.err.println("문의글의 비밀번호가 일치하지 않습니다: " + e.getMessage());
    } catch (RuntimeException e) {
      System.err.println("문의글 수정 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void deleteInquiry(Integer id, String password) {
    try {
      Inquiry inquiry = inquiryDao.findById(id);
      if (!inquiry.getInquiryPassword().equals(password)) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
      }
      inquiryDao.delete(id, password);
    } catch (IllegalArgumentException e) {
      // 비밀번호 불일치 예외 처리
      System.err.println("문의글의 비밀번호가 일치하지 않습니다: " + e.getMessage());
    } catch (RuntimeException e) {
      System.err.println("문의글 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void addPrivateInquiry(String title, String content, String password) {
    try {
      Inquiry inquiry = new Inquiry(title, content, true, password, 2, null); // roleId 2: 회원, isPrivate: true
      inquiryDao.save(inquiry);
    } catch (RuntimeException e) {
      // 비밀 문의글 저장 중 발생한 오류 처리
      System.err.println("비밀 문의글 등록 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void updatePrivateInquiry(Integer id, String title, String content, String password) {
    try {
      Inquiry inquiry = inquiryDao.findById(id);
      if (!inquiry.getInquiryPassword().equals(password)) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
      }
      inquiry.setTitle(title);
      inquiry.setContent(content);
      inquiryDao.update(inquiry);
    } catch (IllegalArgumentException e) {
      // 비밀번호 불일치 예외 처리
      System.err.println("비밀 문의글 수정 중 오류 발생: " + e.getMessage());
    } catch (RuntimeException e) {
      // 비밀 문의글 수정 중 발생한 오류 처리
      System.err.println("비밀 문의글 수정 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void deletePrivateInquiry(Integer id, String password) {
    try {
      Inquiry inquiry = inquiryDao.findById(id);
      if (!inquiry.getInquiryPassword().equals(password)) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
      }
      inquiryDao.delete(id, password);
    } catch (IllegalArgumentException e) {
      // 비밀번호 불일치 예외 처리
      System.err.println("비밀 문의글 삭제 중 오류 발생: " + e.getMessage());
    } catch (RuntimeException e) {
      // 비밀 문의글 삭제 중 발생한 오류 처리
      System.err.println("비밀 문의글 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void deleteInquiryByAdmin(Integer id) {
    try {
      inquiryDao.deleteByAdmin(id);
    } catch (RuntimeException e) {
      // 관리자에 의한 문의글 삭제 중 발생한 오류 처리
      System.err.println("관리자에 의한 문의글 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void replyToPrivateInquiry(Integer inquiryId, String content) {
    try {
      Inquiry inquiry = inquiryDao.findById(inquiryId);
      if (!inquiry.isPrivate()) {
        throw new IllegalArgumentException("비밀 문의글이 아닙니다.");
      }
      // TODO: 답변 로직 추가
    } catch (IllegalArgumentException e) {
      // 잘못된 접근 예외 처리
      System.err.println("1:1 문의 답변 중 오류 발생: " + e.getMessage());
    } catch (RuntimeException e) {
      // 1:1 문의 답변 중 발생한 오류 처리
      System.err.println("1:1 문의 답변 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
