package sk.stasko.ecomerce.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String description;
    private BigDecimal discount;
    private String image;
    private BigDecimal price;
    private String productName;
    private Integer quantity;
    private BigDecimal specialPrice;
}
