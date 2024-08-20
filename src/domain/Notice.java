package domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Notice {
  private Integer id;
  private String title;
  private String content;
  private LocalDateTime createDate;
  private Integer roleId;

  public Notice(String title, String content, Integer roleId) {
    this.title = title;
    this.content = content;
    this.createDate = LocalDateTime.now();
    this.roleId = roleId;
  }
}
