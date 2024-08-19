import domain.Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.RegionService;

import java.util.List;

public class RegionServiceTest {

    private final RegionService regionService = new RegionService();

    @Test
    void findAll() {
        List<Region> regions = regionService.findAllRegions();
        Assertions.assertEquals(regions.size(), 280);
    }

    @Test
    void findById() {
        Region region = regionService.findRegionById(1);
        Assertions.assertEquals(region.getId(), 1);
        Assertions.assertEquals(region.getName(), "서울특별시");
        Assertions.assertEquals(region.getCode(), "11000");
        Assertions.assertNull(region.getParentId());
    }
}
