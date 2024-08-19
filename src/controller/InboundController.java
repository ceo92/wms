package controller;

import domain.*;
import dto.InboundDto;
import service.InboundService;
import service.WarehouseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class InboundController {

    private static final InboundService inboundService = new InboundService();
    private static final WarehouseService warehouseService = new WarehouseService();
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void start(User user) throws IOException {
        RoleType userRole = user.getRoleType();
        boolean exit = false;
        while(!exit) {
            switch (userRole) {
                case ADMIN, WAREHOUSE_MANAGER -> {
                    System.out.println("1. 입고 요청 | 2. 입고 요청 승인 | 3. 입고 요청 취소 | 4. 입고 요청 수정");
                    System.out.println("5. 입고 위치 지정 | 6. 입고 지시서 출력 | 7. 입고 | 8. 입고 현황 | 9. 이전으로");
                    String menu = br.readLine();
                    switch (menu) {
                        case "1" -> {
                            requestInbound(user);
                        }
                        case "2" -> {
                            //approvalInbound();
                        }
                        case "3" -> {
                            //cancelInbound();
                        }
                        case "4" -> {
                            //updateInbound();
                        }
                        case "5" -> {
                            //assignInboundSection();
                        }
                        case "6" -> {
                            //printInboundOrder();
                        }
                        case "7" -> {
                            //processInbound();
                        }
                        case "8" -> {
                            //findInboundStatus();
                        }
                        case "9" -> {
                            exit = true;
                        }
                        default -> {
                            System.out.println("1~9 중에 하나를 입력해주세요.");
                        }
                    }
                }
                case BUSINESS_MAN -> {
                    System.out.println("1. 입고 요청 | 2. 입고 요청 취소 | 3. 입고 요청 수정 | 4. 입고 현황 | 5. 이전으로");
                    String menu = br.readLine();
                    switch (menu) {
                        case "1" -> {
                            requestInbound(user);
                        }
                        case "2" -> {
                            //cancelInbound()
                        }
                        case "3" -> {
                            //updateInbound();
                        }
                        case "4" -> {
                            //findInboundStatus();
                        }
                        case "5" -> {
                            exit = true;
                        }
                        default -> {
                            System.out.println("1~5 중에 하나를 입력해주세요.");
                        }
                    }
                }
                default -> {
                    System.out.println("접근 권한이 없습니다.");
                }
            }
        }

    }
}
