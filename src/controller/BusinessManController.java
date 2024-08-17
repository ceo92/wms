package controller;

import domain.Outbound;
import domain.OutboundType;
import domain.Waybill;
import dto.DispatchDto;
import dto.OutboundDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import service.OutboundService;
import service.WaybillService;

// BusinessManController: 회원 기능 관리
public class BusinessManController {

  private final OutboundService outboundService = new OutboundService();
  private final WaybillService waybillService = new WaybillService();

  public void start() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      System.out.println("1. 출고요청 2. 출고 관리 3. 운송장 조회 4. 종료");
      try {
        int choice = Integer.parseInt(br.readLine());

        switch (choice) {
          case 1:
            requestOutbound(br);
            break;
          case 2:
            manageOutbound(br);
            break;
          case 3:
            viewWaybill(br);
            break;
          case 4:
            return;
          default:
            System.out.println("잘못된 선택입니다.");
        }
      } catch (IOException | NumberFormatException e) {
        System.out.println("오류가 발생했습니다: " + e.getMessage());
      }
    }
  }

  // 출고 요청
  private void requestOutbound(BufferedReader br) {
    try {
      System.out.println("구매자 이름을 입력하세요:");
      String buyerName = br.readLine();

      System.out.println("구매자 지역 ID를 입력하세요:");
      int buyerRegionId = Integer.parseInt(br.readLine());

      System.out.println("구매자 도시를 입력하세요:");
      String buyerCity = br.readLine();

      System.out.println("구매자 상세 주소를 입력하세요:");
      String buyerAddress = br.readLine();

      System.out.println("출고할 상품명을 입력하세요:");
      String productName = br.readLine();

      System.out.println("출고할 수량을 입력하세요:");
      int productQuantity = Integer.parseInt(br.readLine());

      System.out.println("사업자 ID를 입력하세요:");
      int businessManId = Integer.parseInt(br.readLine());

      Outbound outbound = new Outbound(buyerName, buyerRegionId, buyerCity, buyerAddress, productName, productQuantity, OutboundType.WAITINGFORAPPROVAL, businessManId);
      //rs 왜 넣어놨더라...ㅋㅋ
      outboundService.requestOutbound(outbound);
      System.out.println("출고 요청이 성공적으로 등록되었습니다.");
    } catch (IOException | SQLException | NumberFormatException e) {
      System.out.println("출고 요청 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 출고 관리
  private void manageOutbound(BufferedReader br) {
    System.out.println("1. 출고 리스트 보기 2. 출고 상품 검색 3. 출고 지시서 보기 4. 종료");
    try {
      int choice = Integer.parseInt(br.readLine());
      switch (choice) {
        case 1:
          List<OutboundDto> approvedOutbounds = outboundService.viewApprovedOutbounds();
          approvedOutbounds.forEach(outbound -> System.out.println(outbound.toString()));
          break;
        case 2:
          System.out.println("검색할 상품명을 입력하세요:");
          String productName = br.readLine();
          List<OutboundDto> searchResults = outboundService.searchApprovedOutbounds(productName);
          searchResults.forEach(outbound -> System.out.println(outbound.toString()));
          break;
        case 3:
          List<DispatchDto> outboundInstructions = outboundService.viewOutboundInstructions();
          outboundInstructions.forEach(instruction -> System.out.println(instruction.toString()));
          break;
        case 4:
          return;
        default:
          System.out.println("잘못된 선택입니다.");
      }
    } catch (IOException | SQLException | NumberFormatException e) {
      System.out.println("출고 관리 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 운송장 조회
  private void viewWaybill(BufferedReader br) {
    try {
      List<Waybill> waybillList = waybillService.viewAllWaybills();
      waybillList.forEach(waybill -> System.out.println(waybill.toString()));
    } catch (SQLException e) {
      System.out.println("운송장 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
    }
  }
}
