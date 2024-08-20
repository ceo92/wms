package dto;

import domain.Product;
import domain.Stock;
import domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockCreateDto {

    private int stockId;
    private int userId;
    private double width;
    private double height;
    private int quantity;
    private LocalDateTime manufacturedDate;
    private LocalDateTime expirationDate;
    private LocalDate regDate;

}
