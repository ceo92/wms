package domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Region {
    private Integer id;
    private String code;
    private String name;
    private Integer parent;
}
