package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * NoticeDto는 공지사항 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@AllArgsConstructor
public class NoticeDto {
  private String title;
  private String content;
}
