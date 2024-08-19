package controller;

import domain.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import service.InquiryService;
import service.NoticeService;

public class CustomerServiceController {
  private final InquiryService inquiryService = new InquiryService();
  private final NoticeService noticeService = new NoticeService();
  public void start(User user) throws IOException, SQLException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      switch (user.getRoleType()) {
        case ADMIN:
          adminCustomerServiceManagement(br);
          break;
        case WAREHOUSE_MANAGER:
          warehouse_managerCustomerServiceManagement(br);
          break;
        case BUSINESS_MAN:
          business_manCustomerServiceManagement(br);
          break;
        case GUEST:
          guestCustomerServiceManagement(br);
          break;
        case DELIVERY_MAN:
          delivery_manCustomerServiceManagement(br);
          break;
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //admin 고객센터 관리 메서드
  private void adminCustomerServiceManagement(BufferedReader br) throws IOException, SQLException {
    while (true) {
      System.out.println("1. 공지사항 관리 2. 문의글 관리 3. 1:1 문의 댓글 작성 4. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          manageNotices(br);
          break;
        case 2:
          manageAdminInquiries(br);
          break;
        case 3:
          replyToPrivateInquiry(br);
          break;
        case 4:
          return;
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //warehouse_manager 고객센터 관리 메서드
  private void warehouse_managerCustomerServiceManagement(BufferedReader br) throws IOException, SQLException {
    while (true) {
      System.out.println("1. 공지사항 조회 2. 문의글 조회 3. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          noticeService.viewAllNotices();
          break;
        case 2:
          inquiryService.viewAllInquiries();
          break;
        case 3:
          return; // 프로그램 종료
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //business_man 고객센터 관리 메서드
  private void business_manCustomerServiceManagement(BufferedReader br) throws IOException, SQLException {
    while (true) {
      System.out.println("1. 공지사항 조회 2. 문의글 관리 3. 1:1 문의 관리 4. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          noticeService.viewAllNotices();
          break;
        case 2:
          manageInquiries(br);
          break;
        case 3:
          managePrivateInquiries(br);
          break;
        case 4:
          return; // 프로그램 종료
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //guest 고객센터 관리 메서드
  private void guestCustomerServiceManagement(BufferedReader br) throws IOException, SQLException {
    while (true) {
      System.out.println("1. 공지사항 조회 2. 문의글 관리 3. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          noticeService.viewAllNotices();
          break;
        case 2:
          manageInquiries(br);
          break;
        case 3:
          return;
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //delivery_man 고객센터 관리 메서드
  private void delivery_manCustomerServiceManagement(BufferedReader br) throws IOException, SQLException {
    while (true) {
      System.out.println("1. 공지사항 조회 2. 문의글 조회 3. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          noticeService.viewAllNotices();
          break;
        case 2:
          inquiryService.viewAllInquiries();
          break;
        case 3:
          return; // 프로그램 종료
        default:
          System.out.println("잘못된 선택입니다.");
      }
    }
  }

  //공지사항 관리
  private void manageNotices(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 조회 2. 등록 3. 수정 4. 삭제 5. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        noticeService.viewAllNotices();
        break;
      case 2:
        System.out.print("제목: ");
        String title = br.readLine();
        System.out.print("내용: ");
        String content = br.readLine();
        noticeService.addNotice(title, content);
        break;
      case 3:
        System.out.print("수정할 공지사항 ID: ");
        int id = Integer.parseInt(br.readLine());
        System.out.print("새 제목: ");
        title = br.readLine();
        System.out.print("새 내용: ");
        content = br.readLine();
        noticeService.updateNotice(id, title, content);
        break;
      case 4:
        System.out.print("삭제할 공지사항 ID: ");
        id = Integer.parseInt(br.readLine());
        noticeService.deleteNotice(id);
        break;
      case 5:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  //문의글 관리
  private void manageAdminInquiries(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 조회 2. 삭제 3. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        inquiryService.viewAllInquiries();
        break;
      case 2:
        System.out.print("삭제할 문의글 ID: ");
        int id = Integer.parseInt(br.readLine());
        inquiryService.deleteInquiryByAdmin(id);
        break;
      case 3:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  private void manageInquiries(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 조회 2. 작성 3. 수정 4. 삭제 5. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        inquiryService.viewAllInquiries();
        break;
      case 2:
        createInquiry(br);
        break;
      case 3:
        updateInquiry(br);
        break;
      case 4:
        deleteInquiry(br);
        break;
      case 5:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  private void createInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("제목: ");
    String title = br.readLine();
    System.out.print("내용: ");
    String content = br.readLine();
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.addInquiry(title, content, password);
  }

  private void updateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("수정할 문의글 ID: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("비밀번호: ");
    String password = br.readLine();
    System.out.print("새 제목: ");
    String title = br.readLine();
    System.out.print("새 내용: ");
    String content = br.readLine();
    inquiryService.updateInquiry(id, title, content, password);
  }

  private void deleteInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("삭제할 문의글 ID: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.deleteInquiry(id, password);
  }

  //비밀 문의글 댓글
  private void replyToPrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("답변할 1:1 문의 ID: ");
    int inquiryId = Integer.parseInt(br.readLine());
    System.out.print("답변 내용: ");
    String content = br.readLine();
    inquiryService.replyToPrivateInquiry(inquiryId, content);
  }

  //비밀 문의글 관리
  private void managePrivateInquiries(BufferedReader br) throws IOException, SQLException {
    System.out.println("1:1 문의 관리");
    System.out.println("1. 조회 2. 작성 3. 수정 4. 삭제 5. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        viewPrivateInquiries(br);
        break;
      case 2:
        createPrivateInquiry(br);
        break;
      case 3:
        updatePrivateInquiry(br);
        break;
      case 4:
        deletePrivateInquiry(br);
        break;
      case 5:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  //비밀 문의글 조회
  private void viewPrivateInquiries(BufferedReader br) throws IOException, SQLException {
    System.out.println("1:1 문의 조회");
    inquiryService.viewAllPrivateInquiries();
  }

  //비밀 문의글 등록
  private void createPrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("제목: ");
    String title = br.readLine();
    System.out.print("내용: ");
    String content = br.readLine();
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.addPrivateInquiry(title, content, password);
  }

  //비밀 문의글 수정
  private void updatePrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("수정할 1:1 문의글 ID: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("비밀번호: ");
    String password = br.readLine();
    System.out.print("새 제목: ");
    String title = br.readLine();
    System.out.print("새 내용: ");
    String content = br.readLine();
    inquiryService.updatePrivateInquiry(id, title, content, password);
  }

  //비밀문의글 삭제
  private void deletePrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("삭제할 1:1 문의글 ID: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.deletePrivateInquiry(id, password);
  }
}