package dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProfitDto {
    private String name;
    private LocalDate contractDate;
    private Double profit;
}
