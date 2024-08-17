package controller;

import domain.Dispatch;
import domain.Waybill;
import dto.DispatchDto;
import dto.OutboundDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import service.DispatchService;
import service.OutboundService;
import service.WaybillService;

// AdminController: 총관리자 기능 관리
public class AdminController {

  private final DispatchService dispatchService = new DispatchService();
  private final OutboundService outboundService = new OutboundService();
  private final WaybillService waybillService = new WaybillService();

  public void start() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      System.out.println("1. 출고요청 관리 2. 출고 관리 3. 배차 관리 4. 운송장 관리 5. 종료");
      try {
        int choice = Integer.parseInt(br.readLine());

        switch (choice) {
          case 1:
            manageOutboundCheck(br);
            break;
          case 2:
            manageOutbound(br);
            break;
          case 3:
            manageDispatch(br);
            break;
          case 4:
            manageWaybill(br);
            break;
          case 5:
            return;
          default:
            System.out.println("잘못된 선택입니다.");
        }
      } catch (IOException | SQLException | NumberFormatException e) {
        System.out.println("오류가 발생했습니다: " + e.getMessage());
      }
    }
  }

  // 출고요청 관리
  private void manageOutboundCheck(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 미승인 리스트 보기 2. 승인 및 지연 처리 3. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        try {
          List<OutboundDto> nonApprovedOutbounds = outboundService.viewNonApprovedOutbounds();
          nonApprovedOutbounds.forEach(outbound -> System.out.println(outbound.toString()));
        } catch (SQLException e) {
          System.out.println("미승인 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 2:
        try {
          System.out.println("출고 ID를 입력하세요:");
          int outboundId = Integer.parseInt(br.readLine());
          System.out.println("가용 가능한 재고 수량을 입력하세요:");//여기서 가용 가능한재고 가져오기
          int availableStock = Integer.parseInt(br.readLine());
          outboundService.processOutboundApproval(outboundId, availableStock);
        } catch (SQLException | NumberFormatException e) {
          System.out.println("출고 승인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 3:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  // 출고 관리
  private void manageOutbound(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 출고 리스트 보기 2. 출고 상품 검색 3. 출고 지시서 보기 4. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        try {
          List<OutboundDto> approvedOutbounds = outboundService.viewApprovedOutbounds();
          approvedOutbounds.forEach(outbound -> System.out.println(outbound.toString()));
        } catch (SQLException e) {
          System.out.println("출고 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 2:
        try {
          System.out.println("검색할 상품명을 입력하세요:");
          String productName = br.readLine();
          List<OutboundDto> searchResults = outboundService.searchApprovedOutbounds(productName);
          searchResults.forEach(outbound -> System.out.println(outbound.toString()));
        } catch (SQLException e) {
          System.out.println("출고 상품을 검색하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 3:
        try {
          List<DispatchDto> outboundInstructions = outboundService.viewOutboundInstructions();
          outboundInstructions.forEach(instruction -> System.out.println(instruction.toString()));
        } catch (SQLException e) {
          System.out.println("출고 지시서를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 4:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  // 배차 관리
  private void manageDispatch(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 배차등록 2. 배차리스트 조회 3. 배차정보 수정 4. 배차 취소 5. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        try {
          dispatchService.registerDispatch();
          System.out.println("배차 등록이 완료되었습니다.");
        } catch (SQLException e) {
          System.out.println("배차 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 2:
        try {
          List<Dispatch> dispatchList = dispatchService.viewAssignedDispatches();
          dispatchList.forEach(dispatch -> System.out.println(dispatch.toString()));
        } catch (SQLException e) {
          System.out.println("배차 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 3:
        try {
          System.out.println("수정할 배차 ID를 입력하세요:");
          int dispatchId = Integer.parseInt(br.readLine());
          System.out.println("새로운 배송기사 ID를 입력하세요:");
          int newDeliveryManId = Integer.parseInt(br.readLine());
          Delivery_man newDeliveryMan = new Delivery_man(newDeliveryManId);
          dispatchService.modifyDispatch(dispatchId, newDeliveryMan);
          System.out.println("배차 정보가 수정되었습니다.");
        } catch (SQLException e) {
          System.out.println("배차 정보를 수정하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 4:
        try {
          System.out.println("취소할 배차 ID를 입력하세요:");
          int cancelDispatchId = Integer.parseInt(br.readLine());
          dispatchService.cancelDispatch(cancelDispatchId);
          System.out.println("배차가 취소되었습니다.");
        } catch (SQLException e) {
          System.out.println("배차를 취소하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 5:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
    }
  }

  // 운송장 관리
  private void manageWaybill(BufferedReader br) throws IOException, SQLException {
    System.out.println("1. 운송장 등록 2. 운송장 리스트 조회 3. 운송장 수정 4. 종료");
    int choice = Integer.parseInt(br.readLine());
    switch (choice) {
      case 1:
        try {
//          System.out.println("배차 ID를 입력하세요:");
//          Dispatch dispatch = new Dispatch(dispatchId, null, null, DispatchType.ASSIGNED);
          //
          Waybill waybill = new Waybill();
          List<Dispatch> dispatchList = dispatchService.viewAssignedDispatches();
          System.out.println("운송장에 등록할 배차 ID 를 입력해주세요: ");
          int dispatchId = Integer.parseInt(br.readLine());
          for (Dispatch dispatch : dispatchList){
            if (dispatch.getId() == dispatchId){
              waybill.setDispatch(dispatch);
              return;
            }
          }
          waybillService.registerWaybill(waybill);
          System.out.println("운송장 등록이 완료되었습니다.");
        } catch (SQLException e) {
          System.out.println("운송장 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 2:
        viewWaybill(br);
        break;
      case 3:
        try {
          System.out.println("수정할 운송장 ID를 입력하세요:");
          int waybillId = Integer.parseInt(br.readLine());
          System.out.println("새로운 배송기사 ID를 입력하세요:");
          int newDeliveryManId = Integer.parseInt(br.readLine());
          Delivery_man newDeliveryMan = new Delivery_man(newDeliveryManId);
          waybillService.modifyWaybill(waybillId, newDeliveryMan);
          System.out.println("운송장 정보가 수정되었습니다.");
        } catch (SQLException e) {
          System.out.println("운송장 정보를 수정하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        break;
      case 4:
        return;
      default:
        System.out.println("잘못된 선택입니다.");
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
