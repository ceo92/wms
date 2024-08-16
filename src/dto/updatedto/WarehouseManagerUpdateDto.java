package dto.updatedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class WarehouseManagerUpdateDto {
  private String name; //사업자 이름
  private String phoneNumber; //핸드폰 번호
}
