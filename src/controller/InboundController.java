package controller;

import domain.*;
import dto.InboundDto;
import dto.InboundItemDto;
import service.InboundService;
import service.ProductService;
import service.StockService;
import service.WarehouseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InboundController {

    private final InboundService inboundService = new InboundService();
    private final WarehouseService warehouseService = new WarehouseService();
    private final ProductService productService = new ProductService();
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        InboundController ic = new InboundController();
        ic.start(new User(1, RoleType.ADMIN));
    }

    public void start(User user) {
        try {
            RoleType userRole = user.getRoleType();
            boolean exit = false;
            while(!exit) {
                switch (userRole) {
                    case ADMIN, WAREHOUSE_MANAGER -> {
                        System.out.println("===========================================================================================================================================");
                        System.out.println("1. 입고 요청 | 2. 입고 요청 승인 | 3. 입고 요청 취소 | 4. 입고 요청 수정 | 5. 입고 위치 지정 | 6. 입고 지시서 출력 | 7. 입고 | 8. 입고 현황 | 9. 이전으로");
                        System.out.println("===========================================================================================================================================");
                        String menu = br.readLine();
                        switch (menu) {
                            case "1" -> {
                                System.out.println("[입고 요청]");
                                List<Warehouse> warehouseList = warehouseService.findAllWarehouses();
                                System.out.println("1) 입고 물류센터 선택");
                                Warehouse warehouse = selectWarehouse(warehouseList);
                                System.out.println("2) 공급사 선택");
                                List<Vendor> vendors = productService.findAllVendors();
                                Vendor vendor = selectVendor(vendors);
                                System.out.println("3) 공급사별 입고 품목 선택");
                                List<Product> productList = productService.findAllByVendorId(vendor.getId());
                                List<InboundItemDto> items = selectInboundItem(productList);
                                if(items.isEmpty()) {
                                    throw new IllegalArgumentException("입고 품목이 선택되지 않았습니다.");
                                }
                                System.out.println("4) 입고 예상 날짜 지정");
                                System.out.print("입고 예상 날짜(yyyy-mm-dd): ");
                                String inboundExpectedDate = br.readLine();
                                InboundDto inbound = InboundDto.builder()
                                        .inboundExpectedDate(LocalDate.parse(inboundExpectedDate))
                                        .userId(user.getId())
                                        .vendorId(vendor.getId())
                                        .warehouseId(warehouse.getId())
                                        .build();
                                if(inboundService.saveInbound(inbound, items)) {
                                    System.out.println("입고 요청이 완료되었습니다.");
                                    System.out.println();
                                }
                            }
                            case "2" -> {
                                System.out.println("[입고 요청 승인]");
                                List<Inbound> inboundList = inboundService.findInboundsByStatus(InboundStatus.PENDING);
                                approvalInbound(inboundList);
                            }
                            case "3" -> {
                                System.out.println("[입고 요청 취소]");
                                List<Inbound> inboundList = inboundService.findInboundsByStatus(InboundStatus.PENDING);
                                cancelInbound(inboundList);
                            }
                            case "4" -> {
                                System.out.println("[입고 요청 수정]");
                                List<Inbound> inboundList = inboundService.findInboundsByStatus(InboundStatus.PENDING);
                                updateInbound(inboundList);
                            }
                            case "5" -> {
                                System.out.println("[입고 위치 지정]");
                                assignInboundSection();
                            }
                            case "6" -> {
                                System.out.println("[입고 지시서 출력]");
                                printInboundOrder();
                            }
                            case "7" -> {
                                System.out.println("[입고]");
                                processInbound();
                            }
                            case "8" -> {
                                System.out.println("[입고 현황]");
                                searchInbound();
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
                                System.out.println("[입고 요청]");
                                System.out.println("1) 입고 물류센터 선택");
                                List<WarehouseContract> contracts = warehouseService.findWarehouseContractByUserId(user.getId());
                                if(contracts.isEmpty()) {
                                    System.out.println("계약된 창고 정보가 없습니다.");
                                    return;
                                }
                                List<Warehouse> warehouseList = contracts.stream().map(WarehouseContract::getWarehouse).collect(Collectors.toList());
                                Warehouse warehouse = selectWarehouse(warehouseList);
                                System.out.println("2) 공급사 선택");
                                List<Vendor> vendors = productService.findAllVendors();
                                Vendor vendor = selectVendor(vendors);
                                System.out.println("3) 공급사별 입고 품목 선택");
                                List<Product> productList = productService.findAllByVendorId(vendor.getId());
                                List<InboundItemDto> items = selectInboundItem(productList);
                                if(items.isEmpty()) {
                                    throw new IllegalArgumentException("입고 품목이 선택되지 않았습니다.");
                                }
                                System.out.println("4) 입고 예상 날짜 지정");
                                System.out.print("입고 예상 날짜(yyyy-mm-dd): ");
                                String inboundExpectedDate = br.readLine();
                                InboundDto inbound = InboundDto.builder()
                                        .inboundExpectedDate(LocalDate.parse(inboundExpectedDate))
                                        .userId(user.getId())
                                        .vendorId(vendor.getId())
                                        .warehouseId(warehouse.getId())
                                        .build();
                                if(inboundService.saveInbound(inbound, items)) {
                                    System.out.println("입고 요청이 완료되었습니다.");
                                    System.out.println();
                                }
                            }
                            case "2" -> {
                                System.out.println("[입고 요청 취소]");
                                List<Inbound> inboundList = inboundService.findInboundByUserId(user.getId());
                                cancelInbound(inboundList);
                            }
                            case "3" -> {
                                System.out.println("[입고 요청 수정]");
                                List<Inbound> inboundList = inboundService.findInboundByUserId(user.getId());
                                updateInbound(inboundList);
                            }
                            case "4" -> {
                                System.out.println("[입고 현황]");
                                List<Inbound> inboundList = inboundService.findInboundByUserId(user.getId());
                                printInboundList(inboundList);
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
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void processInbound() throws IOException {
        List<Inbound> inbounds = inboundService.findInboundsByStatus(InboundStatus.APPROVED);
        if(inbounds.isEmpty()) {
            System.out.println("입고 처리 가능한 승인된 입고 건이 없습니다.");
            return;
        }
        printInboundList(inbounds);
        System.out.print("입고 처리할 입고 건의 no: ");
        int no = Integer.parseInt(br.readLine());
        Inbound inbound = inbounds.get(no - 1);
        printInboundDetail(inbound);
        List<InboundItem> items = inboundService.findInboundItems(inbound.getId());
        printInboundItemList(items);
        System.out.print("입고 처리할 입고 품목의 no: ");
        int itemNo = Integer.parseInt(br.readLine());
        InboundItem item = items.get(itemNo - 1);

        System.out.print("가로 길이: ");
        double width = Double.parseDouble(br.readLine());
        System.out.print("세로 길이: ");
        double height = Double.parseDouble(br.readLine());
        System.out.print("입고 수량: ");
        int quantity = Integer.parseInt(br.readLine());
        System.out.print("제조 일자(yyyy-mm-dd): ");
        LocalDateTime manufacturedDate = LocalDate.parse(br.readLine()).atStartOfDay();
        System.out.print("유효 일자(yyyy-mm-dd): ");
        LocalDateTime expirationDate = LocalDate.parse(br.readLine()).atStartOfDay();

        Stock stock = Stock.builder()
                .product(item.getProduct())
                .user(inbound.getUser())
                .width(width)
                .height(height)
                .quantity(quantity)
                .regDate(LocalDateTime.now())
                .expirationDate(expirationDate)
                .manufacturedDate(manufacturedDate)
                .build();
        if(inboundService.confirmInbound(item.getId(), stock)) {
            System.out.println("입고 처리 성공");
        } else {
            System.out.println("입고 처리 실패");
        }
    }

    private void assignInboundSection() throws IOException {
        List<Warehouse> warehouses = warehouseService.findAllWarehouses();
        System.out.println("1) 입고 위치를 지정할 창고 선택");
        Warehouse warehouse = selectWarehouse(warehouses);
        //inboundService.assignInboundSection();
    }

    private void printInboundOrder() throws IOException {
        List<Inbound> inbounds = inboundService.findAllInbounds();
        printInboundList(inbounds);
        System.out.print("지시서를 출력할 입고 건의 no: ");
        int no = Integer.parseInt(br.readLine());
        Inbound inbound = inbounds.get(no - 1);
        String order = inboundService.generateInboundOrder(inbound.getId());
        System.out.println(order);
    }

    private void searchInbound() throws IOException {
        System.out.println("1. 기간별 입고 현황 | 2. 월별 입고 현황 | 3. 이전으로");
        String menu = br.readLine();
        switch (menu) {
            case "1" -> {
                System.out.print("시작 날짜 입력(yyyy-mm-dd): ");
                String startDate = br.readLine();
                System.out.print("종료 날짜 입력(yyyy-mm-dd): ");
                String endDate = br.readLine();
                List<Inbound> inbounds = inboundService.findInboundsByPeriod(startDate, endDate);
                printInboundList(inbounds);
                System.out.println("no: ");
                int no = Integer.parseInt(br.readLine());
                Inbound inbound = inbounds.get(no - 1);
                printInboundDetail(inbound);
                List<InboundItem> inboundItems = inboundService.findInboundItems(inbound.getId());
                printInboundItemList(inboundItems);
            }
            case "2" -> {
                System.out.print("연도 입력: ");
                String year = br.readLine();
                System.out.print("월 입력(1~12): ");
                String month = br.readLine();
                List<Inbound> inbounds = inboundService.findInboundsByYearMonth(year, month);
                printInboundList(inbounds);
                System.out.println("no: ");
                int no = Integer.parseInt(br.readLine());
                Inbound inbound = inbounds.get(no - 1);
                printInboundDetail(inbound);
                List<InboundItem> inboundItems = inboundService.findInboundItems(inbound.getId());
                printInboundItemList(inboundItems);
            }
            case "3" -> {

            }
            default -> System.out.println("1~3 중에 입력해주세요.");
        }

    }

    private void updateInbound(List<Inbound> inboundList) throws IOException {
        printInboundList(inboundList);
        System.out.println("수정할 입고 건의 no: ");
        int no = Integer.parseInt(br.readLine());
        Inbound inbound = inboundList.get(no - 1);
        printInboundDetail(inbound);
        List<InboundItem> inboundItems = inboundService.findInboundItems(inbound.getId());
        printInboundItemList(inboundItems);
        System.out.println("1. 입고 예상 날짜 수정 | 2. 입고 품목 수량 수정 | 3. 입고 품목 삭제 | 4. 이전으로");
        String menu = br.readLine();
        switch (menu) {
            case "1" -> {
                System.out.print("수정할 날짜(yyyy-mm-dd): ");
                String date = br.readLine();
                if(inboundService.updateExpectedDate(inbound.getId(), date)) {
                    System.out.println("입고 예상 날짜 수정 성공");
                } else {
                    System.out.println("입고 예상 날짜 수정 실패");
                }
            }
            case "2" -> {
                System.out.print("수정할 품목 건의 no: ");
                int updateNo = Integer.parseInt(br.readLine());
                InboundItem item = inboundItems.get(updateNo - 1);
                System.out.print("변경 수량: ");
                int updateQuantity = Integer.parseInt(br.readLine());
                if(inboundService.updateItemRequestQuantity(item.getId(), updateQuantity)) {
                    System.out.println("품목 수량 수정 성공");
                } else {
                    System.out.println("품목 수량 수정 실패");
                }
            }
            case "3" -> {
                System.out.print("삭제할 품목 건의 no: ");
                int delNo = Integer.parseInt(br.readLine());
                InboundItem item = inboundItems.get(delNo - 1);
                if(inboundService.deleteInboundItem(item.getId())) {
                    System.out.println("입고 품목 삭제 성공");
                } else {
                    System.out.println("입고 품목 삭제 실패");
                }
            }
            case "4" -> {
            }
            default -> System.out.println("1~4 중에 입력해주세요.");
        }
    }

    private void printInboundDetail(Inbound inbound) {
        if(inbound.getUser() instanceof BusinessMan businessMan) {
            System.out.println("화주사: " + businessMan.getBusinessName());
        }
        System.out.println("물류센터: " + inbound.getWarehouse().getName());
        System.out.println("입고번호: " + inbound.getId());
        System.out.println("공급사: " + inbound.getVendor().getName());
        System.out.println("입고예정일자: " + inbound.getInboundExpectedDate());
    }

    private void cancelInbound(List<Inbound> inboundList) throws IOException {
        printInboundList(inboundList);
        System.out.println("취소할 입고 건의 no: ");
        int no = Integer.parseInt(br.readLine());
        Inbound inbound = inboundList.get(no - 1);
        try {
            inboundService.approvalInbound(inbound.getId());
            System.out.println("입고 취소 완료");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void approvalInbound(List<Inbound> inboundList) throws IOException {
        printInboundList(inboundList);
        System.out.println("승인할 입고 건의 no: ");
        int no = Integer.parseInt(br.readLine());
        Inbound inbound = inboundList.get(no - 1);
        try {
            inboundService.approvalInbound(inbound.getId());
            System.out.println("입고 승인 완료");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printInboundList(List<Inbound> inboundList) {
        System.out.printf("%-3s %-15s %-20s %-10s %-15s\n", "no", "창고명", "공급사명", "요청날짜", "상태");
        AtomicInteger no = new AtomicInteger(1);
        inboundList.forEach(inbound -> {
            System.out.printf("%-3s %-15s %-20s %-10s %-15s\n",
                    no.getAndIncrement(), inbound.getWarehouse().getName(), inbound.getVendor().getName(), inbound.getRegDate().toLocalDate(), inbound.getStatus().getValue());
        });
    }

    private Vendor selectVendor(List<Vendor> vendors) throws IOException {
        System.out.printf("%-3s %-20s\n", "no", "공급사명");
        System.out.println("-------------------------------------------------------------------------");
        AtomicInteger no = new AtomicInteger(1);
        vendors.forEach(vendor -> {
            System.out.printf("%-3d %-20s\n", no.getAndIncrement(), vendor.getName());
        });
        System.out.println("-------------------------------------------------------------------------");
        System.out.print("선택할 공급사의 no: ");
        int wNo = Integer.parseInt(br.readLine());
        return vendors.get(wNo - 1);
    }

    private List<InboundItemDto> selectInboundItem(List<Product> productList) throws IOException {
        printProductList(productList);
        List<InboundItemDto> items = new ArrayList<>();
        while(true) {
            System.out.println("1. 입고 품목 추가 | 2. 입고 품목 삭제 | 3. 입고 요청");
            String menu = br.readLine();
            switch (menu) {
                case "1" -> {
                    System.out.print("추가할 제품의 no: ");
                    int pNo = Integer.parseInt(br.readLine());
                    Product selectProduct = productList.get(pNo - 1);
                    System.out.println("선택 제품명: " + selectProduct.getName());
                    System.out.print("수량: ");
                    int requestQuantity = Integer.parseInt(br.readLine());
                    InboundItemDto dto = new InboundItemDto(selectProduct.getId(), selectProduct.getName(), requestQuantity);
                    items.add(dto);
                    this.printInboundItemDtoList(items);
                }
                case "2" -> {
                    if(items.isEmpty()) {
                        System.out.println("삭제할 품목이 존재하지 않습니다.");
                    } else {
                        this.printInboundItemDtoList(items);
                        System.out.print("삭제할 제품의 no: ");
                        int deleteIndex = Integer.parseInt(br.readLine());
                        if(deleteIndex < 1 || deleteIndex > items.size()) {
                            System.out.println("유효하지 않은 no 값입니다.");
                        } else {
                            items.remove(deleteIndex - 1);
                            this.printInboundItemDtoList(items);
                        }
                    }
                }
                case "3" -> {
                    return items;
                }
                default -> System.out.println("1, 2, 3 중에 입력해주세요.");
            }

        }

    }

    private void printProductList(List<Product> productList) {
        System.out.printf("%-3s %-10s %-20s %-50s %-10s %-20s %-15s\n", "no", "제품코드", "카테고리", "제품명", "원가", "제조사", "공급사");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
        AtomicInteger no = new AtomicInteger(1);
        productList.forEach(product -> {
            System.out.printf("%-3s %-10s %-20s %-50s %-10s %-20s %-15s\n",
                    no.getAndIncrement(), product.getCode(), product.getProductCategory().getName(), product.getName(), product.getCostPrice(), product.getManufacturer(), product.getVendor().getName());
        });
        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
    }

    private void printInboundItemDtoList(List<InboundItemDto> items) {
        System.out.println("------------------------------------------------------ 입고 품목 ------------------------------------------------------");
        System.out.printf("%-3s %-50s %-20s\n", "no", "제품명", "요청수량");
        for(int i = 0; i < items.size(); i++) {
            InboundItemDto item = items.get(i);
            System.out.printf("%-3s %-50s %-20s\n", i + 1, item.getProductName(), item.getRequestQuantity());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }

    private void printInboundItemList(List<InboundItem> inboundItems) {
        System.out.println("------------------------------------ 입고 품목 ------------------------------------");
        System.out.printf("%-3s %-15s %-50s %-8s %-8s\n", "no", "상품코드", "상품명", "요청수량", "완료수량");
        AtomicInteger noCnt = new AtomicInteger(1);
        inboundItems.forEach(item -> System.out.printf("%-3s %-15s %-50s %-8s %-8s\n",
                noCnt.getAndIncrement(),
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getRequestQuantity(),
                item.getCompleteQuantity()));
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private Warehouse selectWarehouse(List<Warehouse> warehouseList) throws IOException {
        System.out.printf("%-3s %-20s %-10s %-15s\n", "no", "창고명", "종류", "소재지");
        System.out.println("-------------------------------------------------------------------------");
        AtomicInteger no = new AtomicInteger(1);
        warehouseList.forEach(item -> {
            System.out.printf("%-3d %-20s %-10s %-15s\n", no.getAndIncrement(), item.getName(), item.getType().getName(), item.getRegion().getName());
        });
        System.out.println("-------------------------------------------------------------------------");
        System.out.print("no: ");
        int wNo = Integer.parseInt(br.readLine());
        return warehouseList.get(wNo - 1);
    }

}
