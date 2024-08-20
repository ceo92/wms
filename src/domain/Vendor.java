package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class Vendor {

    private Integer id;
    private String name;

    public Vendor(int id) {
        this.id = id;
    }

}
