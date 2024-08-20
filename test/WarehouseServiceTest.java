import domain.Region;
import domain.User;
import domain.Warehouse;
import domain.WarehouseType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.WarehouseService;

import java.time.LocalDateTime;
import java.util.UUID;

public class WarehouseServiceTest {

    private final WarehouseService warehouseService = new WarehouseService();

    @Test
    void save() {
        String name = "용인 물류센터";
        String code = "WH-" + UUID.randomUUID().toString().substring(0, 7);
        User manager = User.builder().id(2).build();
        WarehouseType type = new WarehouseType(1, "냉장");
        Region region = Region.builder().id(121).build();
        String detailAddress = "평화로 123번길 45";
        String contact = "0313331414";
        double maxCapacity = 5000;
        double pricePerArea = 3200.00;
        LocalDateTime regDate = LocalDateTime.now();

        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .code(code)
                .manager(manager)
                .type(type)
                .region(region)
                .detailAddress(detailAddress)
                .contact(contact)
                .maxCapacity(maxCapacity)
                .pricePerArea(pricePerArea)
                .regDate(regDate)
                .build();
        int id = warehouseService.saveWarehouse(warehouse);

        Warehouse findWarehouse = warehouseService.findWarehouseById(id);
        Assertions.assertEquals(findWarehouse.getName(), name);
        Assertions.assertEquals(findWarehouse.getCode(), code);
        Assertions.assertEquals(findWarehouse.getManager().getId(), manager.getId());
        Assertions.assertEquals(findWarehouse.getType().getId(), type.getId());
        Assertions.assertEquals(findWarehouse.getContact(), contact);
        Assertions.assertEquals(findWarehouse.getRegion().getId(), region.getId());
        Assertions.assertEquals(findWarehouse.getDetailAddress(), detailAddress);
        Assertions.assertEquals(findWarehouse.getMaxCapacity(), maxCapacity);
        Assertions.assertEquals(findWarehouse.getPricePerArea(), pricePerArea);
    }
}
