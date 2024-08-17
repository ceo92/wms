package controller;

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

// DeliveryManController: 배송기사 기능 관리
public class DeliveryManController {

  private final OutboundService outboundService = new OutboundService();
  private final WaybillService waybillService = new WaybillService();

  public void start() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      System.out.println("1. 출고 리스트 조회 2. 출고 지시서 조회 3. 운송장 조회 4. 종료");
      try {
        int choice = Integer.parseInt(br.readLine());

        switch (choice) {
          case 1:
            viewOutboundList(br);
            break;
          case 2:
            viewOutboundInstruction(br);
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

  // 출고 리스트 조회
  private void viewOutboundList(BufferedReader br) {
    try {
      List<OutboundDto> approvedOutbounds = outboundService.viewApprovedOutbounds();
      approvedOutbounds.forEach(outbound -> System.out.println(outbound.toString()));
    } catch (SQLException e) {
      System.out.println("출고 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 출고 지시서 조회
  private void viewOutboundInstruction(BufferedReader br) {
    try {
      List<DispatchDto> outboundInstructions = outboundService.viewOutboundInstructions();
      outboundInstructions.forEach(instruction -> System.out.println(instruction.toString()));
    } catch (SQLException e) {
      System.out.println("출고 지시서를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
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
