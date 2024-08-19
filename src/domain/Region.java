package domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Region {
  private Integer id;
  private String code;
  private String name;
  private Integer parentId; //이건 기본 키 필드가 아니라 그냥 일반 필드에 parendId란 이름인 것임 Integer도 null을 대비해서 넣은 거고 그냥 일반 필드다 !

}
