package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import service.InquiryService;
import service.NoticeService;

public class DeliveryManController {

  private final NoticeService noticeService = new NoticeService();
  private final InquiryService inquiryService = new InquiryService();

  public void start() throws IOException, SQLException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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
}
