package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import service.InquiryService;
import service.NoticeService;

public class AdminController {

  private final NoticeService noticeService = new NoticeService();
  private final InquiryService inquiryService = new InquiryService();

  public void start() throws IOException, SQLException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      System.out.println("1. 공지사항 관리 2. 문의글 관리 3. 1:1 문의 댓글 작성 4. 종료");
      int choice = Integer.parseInt(br.readLine());

      switch (choice) {
        case 1:
          manageNotices(br);
          break;
        case 2:
          manageInquiries(br);
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

  private void manageInquiries(BufferedReader br) throws IOException, SQLException {
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

  private void replyToPrivateInquiry(BufferedReader br) throws IOException, SQLException {
    System.out.print("답변할 1:1 문의 ID: ");
    int inquiryId = Integer.parseInt(br.readLine());
    System.out.print("답변 내용: ");
    String content = br.readLine();
    inquiryService.replyToPrivateInquiry(inquiryId, content);
  }
}
