package dto.updatedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BusinessManUpdateDto {
  private String name;
  private String phoneNumber;
  private String businessNum; //사업자번호
  private String businessName; //상호명
}
