package controller;

import domain.*;
import service.RegionService;
import service.WarehouseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class WarehouseController {

    private final WarehouseService warehouseService = new WarehouseService();
    private final RegionService regionService = new RegionService();
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        WarehouseController wc = new WarehouseController();
        wc.start(new User(1, RoleType.ADMIN));
    }

    public void start(User user) throws IOException {
        RoleType userRole = user.getRoleType();
        boolean exit = false;
        while(!exit) {
            switch (userRole) {
                case ADMIN, WAREHOUSE_MANAGER -> {
                    System.out.println("1. 창고 등록 | 2. 창고 조회 | 3. 이전으로");
                    String menu = br.readLine();
                    switch (menu) {
                        case "1" -> {
                            System.out.println("[창고 등록]");
                            registerWarehouse(user);
                        }
                        case "2" -> {
                            System.out.println("1. 소재지별 창고 조회 | 2. 창고명별 창고 조회 | 3. 창고종류별 창고 조회 | 4. 이전으로");
                            String readMenu = br.readLine();
                            switch (readMenu) {
                                case "1" -> {
                                    System.out.println("[소재지별 창고 조회]");
                                    searchByRegion();
                                }
                                case "2" -> {
                                    System.out.println("[창고명별 창고 조회]");
                                    searchByWarehouseName();
                                }
                                case "3" -> {
                                    System.out.println("[창고종류별 창고 조회]");
                                    searchByWarehouseType();
                                }
                                case "4" -> {
                                }
                                default -> {
                                    System.out.println("1~4 중에 입력해주세요.");
                                }
                            }
                        }
                        case "3" -> {
                            exit = true;;
                        }
                        default -> {
                            System.out.println("1~3 중에 하나를 입력해주세요.");
                        }
                    }
                }
                case BUSINESS_MAN -> {
                    System.out.println("1. 창고 계약 | 2. 창고 계약 조회 | 3. 이전으로");
                    String menu = br.readLine();
                    switch (menu) {
                        case "1" -> {
                            System.out.println("[창고 계약]");
                            createWarehouseContract(user);
                        }
                        case "2" -> {
                            //findWarehouseContract(user);
                        }
                        case "3" -> {
                            exit = true;
                        }
                        default -> {
                            System.out.println("1~3 중에 하나를 입력해주세요.");
                        }
                    }
                }
                default -> {
                    System.out.println("접근 권한이 없습니다.");
                }
            }
        }

    }

    private void createWarehouseContract(User user) {

    }

    private void registerWarehouse(User user) throws IOException {
        System.out.print("창고명: ");
        String name = br.readLine();
        System.out.print("연락처: ");
        String contact = br.readLine();
        System.out.print("총면적: ");
        double maxCapacity = Double.parseDouble(br.readLine());
        System.out.print("단위면적당 대여비용: ");
        double pricePerArea = Double.parseDouble(br.readLine());
        System.out.println("<창고종류 선택>");
        List<WarehouseType> types = warehouseService.findAllWarehouseType();
        types.forEach(type -> {
            System.out.println(type.getId() + ". " + type.getName());
        });
        System.out.print("창고종류 번호: ");
        int typeIndex = Integer.parseInt(br.readLine());
        WarehouseType type = types.get(typeIndex - 1);
        System.out.println("<창고위치 선택>");
        Region selectedRegion = selectRegion();
        System.out.println(selectedRegion.getName());
        System.out.print("상세주소: ");
        String detailAddress = br.readLine();

        Warehouse warehouse = Warehouse.builder()
                .manager(user)
                .type(type)
                .name(name)
                .code(warehouseService.generateWarehouseCode())
                .region(selectedRegion)
                .detailAddress(detailAddress)
                .contact(contact)
                .maxCapacity(maxCapacity)
                .pricePerArea(pricePerArea)
                .regDate(LocalDateTime.now())
                .build();
        warehouseService.saveWarehouse(warehouse);
        System.out.println("창고 등록에 성공했습니다.");
    }

    private void searchByWarehouseType() throws IOException {
        List<WarehouseType> warehouseTypes = warehouseService.findAllWarehouseType();
        AtomicInteger counter = new AtomicInteger(1);
        warehouseTypes.forEach(parent -> {
            System.out.printf("%3d %20s\n", counter.getAndIncrement(), parent.getName());
        });
        System.out.print("선택: ");
        int no = Integer.parseInt(br.readLine());
        WarehouseType selectedType = warehouseTypes.get(no - 1);
        List<Warehouse> warehouseListByType = warehouseService.searchWarehousesByTypeId(selectedType.getId());
        printWarehouseList(warehouseListByType);
    }

    private void searchByWarehouseName() throws IOException {
        System.out.print("창고명: ");
        String name = br.readLine();
        List<Warehouse> warehouseListByName = warehouseService.searchWarehousesByName(name);
        printWarehouseList(warehouseListByName);
    }

    private void searchByRegion() throws IOException {
        int regionId = selectRegion().getId();
        List<Warehouse> warehouseListByRegion = warehouseService.searchWarehousesByRegionId(regionId);
        printWarehouseList(warehouseListByRegion);
    }

    private Region selectRegion() throws IOException {
        List<Region> regions = regionService.findAllRegions();
        List<Region> parents = regions.stream()
                .filter(region -> region.getParentId() == null)
                .toList();
        System.out.println("== 지역 선택: 시/도 ==");
        AtomicInteger counter = new AtomicInteger(1);
        parents.forEach(parent -> {
            System.out.printf("%3d %20s\n", counter.getAndIncrement(), parent.getName());
        });
        System.out.print("선택: ");
        int no1 = Integer.parseInt(br.readLine());
        Region selectedParent = parents.get(no1 - 1);

        System.out.println("== 지역 선택: 시/군/구 ==");
        List<Region> children = regions.stream()
                .filter(region -> Objects.equals(region.getParentId(), selectedParent.getId()))
                .toList();
        counter.set(1);
        children.forEach(child -> {
            System.out.printf("%3d %20s\n", counter.getAndIncrement(), child.getName());
        });
        System.out.print("선택: ");
        int no2 = Integer.parseInt(br.readLine());
        return children.get(no2 - 1);
    }

    private void printWarehouseList(List<Warehouse> list) throws IOException {
        System.out.printf("%-3s %-20s %-10s %-15s\n", "no", "창고명", "종류", "소재지");
        System.out.println("-------------------------------------------------------------------------");
        AtomicInteger no = new AtomicInteger(1);
        list.forEach(item -> {
            System.out.printf("%-3d %-20s %-10s %-15s\n", no.getAndIncrement(), item.getName(), item.getType().getName(), item.getRegion().getName());
        });
        System.out.println("-------------------------------------------------------------------------");
        if(list.size() > 0) {
            System.out.println("1. 상세보기 | 2. 이전으로");
            String menu = br.readLine();
            switch (menu) {
                case "1" -> {
                    System.out.print("no: ");
                    int selectedNo = Integer.parseInt(br.readLine());
                    printWarehouseDetail(list.get(selectedNo - 1));
                }
                case "2" -> {}
                default -> System.out.println("1, 2 중에서 입력해주세요.");
            }
        }
    }

    private void printWarehouseDetail(Warehouse warehouse) {
        System.out.println();
        System.out.println("================== 창고 상세정보 ==================");
        System.out.println("창고명: " + warehouse.getName());
        System.out.println("창고코드: " + warehouse.getCode());
        System.out.println("창고종류: " + warehouse.getType().getName());
        System.out.println("창고위치: " + warehouse.getRegion().getName() + " " + warehouse.getDetailAddress());
        System.out.println("연락처: " + warehouse.getContact());
        System.out.println("총면적: " + warehouse.getMaxCapacity());
        System.out.println("단위면적당 대여비용: " + warehouse.getPricePerArea());
        System.out.println("등록일: " + warehouse.getRegDate().toLocalDate());
        System.out.println("=================================================");
    }

}
