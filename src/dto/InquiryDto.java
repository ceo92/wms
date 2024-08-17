package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * InquiryDto는 문의글 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@AllArgsConstructor
public class InquiryDto {
  private String title;
  private String content;
  private boolean isPrivate;
  private String inquiryPassword;
  private Integer guestId;
}
