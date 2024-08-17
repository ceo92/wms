package domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Inquiry 클래스는 문의글 정보를 포함합니다.
 */
@Getter
@Setter
public class Inquiry {
  private Integer id;
  private String title;
  private String content;
  private LocalDateTime createDate;
  private boolean isPrivate;
  private String inquiryPassword;
  private Integer roleId;
  private Integer guestId;

  public Inquiry(String title, String content, boolean isPrivate, String inquiryPassword, Integer roleId, Integer guestId) {
    this.title = title;
    this.content = content;
    this.createDate = LocalDateTime.now();
    this.isPrivate = isPrivate;
    this.inquiryPassword = inquiryPassword;
    this.roleId = roleId;
    this.guestId = guestId;
  }
}
