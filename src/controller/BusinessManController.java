package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import service.InquiryService;
import service.NoticeService;


public class BusinessManController {

  private final NoticeService noticeService = new NoticeService();
  private final InquiryService inquiryService = new InquiryService();

  public void start() throws IOException, SQLException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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

  private void viewPrivateInquiries(BufferedReader br) throws IOException, SQLException {
    System.out.println("1:1 문의 조회");
    inquiryService.viewAllPrivateInquiries();
  }

  private void createPrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("제목: ");
    String title = br.readLine();
    System.out.print("내용: ");
    String content = br.readLine();
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.addPrivateInquiry(title, content, password);
  }

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

  private void deletePrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("삭제할 1:1 문의글 ID: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("비밀번호: ");
    String password = br.readLine();
    inquiryService.deletePrivateInquiry(id, password);
  }
}
