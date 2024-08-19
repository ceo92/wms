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
    private Integer parentId;
}
